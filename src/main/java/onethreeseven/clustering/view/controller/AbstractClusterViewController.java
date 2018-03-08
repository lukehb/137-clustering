package onethreeseven.clustering.view.controller;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import onethreeseven.trajsuitePlugin.model.BaseTrajSuiteProgram;
import onethreeseven.trajsuitePlugin.model.BoundingCoordinates;
import onethreeseven.trajsuitePlugin.model.EntitySupplier;
import onethreeseven.trajsuitePlugin.model.Layers;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Abstract view controller for clustering views.
 * @author Luke Bermingham
 */
public abstract class AbstractClusterViewController {

    protected final AtomicReference<double[][]> ptsToCluster = new AtomicReference<>(null);
    protected final AtomicBoolean updatingView = new AtomicBoolean(false);

    public AbstractClusterViewController(){
        Layers layers = BaseTrajSuiteProgram.getInstance().getLayers();

        layers.numEditedEntitiesProperty.addListener((observable, oldValue, newValue) -> AbstractClusterViewController.this.calculatePointsToCluster());
        layers.addEntitiesTransactionProperty.addListener((observable, oldValue, newValue) -> AbstractClusterViewController.this.calculatePointsToCluster());
        layers.removeEntitiesTransactionProperty.addListener((observable, oldValue, newValue) -> AbstractClusterViewController.this.calculatePointsToCluster());

        //when the view is shown initialise the points to cluster
        CompletableFuture.runAsync(()->{
            while(true){
                if(isShowing()){
                    calculatePointsToCluster();
                    return;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    protected void calculatePointsToCluster(){

        //already updating view
        if(updatingView.get()){
            return;
        }

        //don't do anything if not showing
        if(!isShowing()){
            return;
        }

        ThreadFactory threadFactory = r -> new Thread(r, "Selected entities to points 2d");

        ExecutorService exec = Executors.newSingleThreadExecutor(threadFactory);

        Runnable task = () -> {

            updatingView.set(true);

            //sleep 100ms to wait for selection updates to come in

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ArrayList<double[]> pts = new ArrayList<>();
            int selectedEntities = 0;

            ServiceLoader<EntitySupplier> serviceLoader = ServiceLoader.load(EntitySupplier.class);
            for (EntitySupplier entitySupplier : serviceLoader) {
                Map<Class, Collection<Object>> allSelected = entitySupplier.supplyAllSelected();
                if(allSelected != null){
                    for (Collection<Object> entityCol : allSelected.values()) {
                        for (Object entityObj : entityCol) {
                            if(entityObj instanceof BoundingCoordinates){
                                selectedEntities++;
                                Iterator<double[]> coordIter = ((BoundingCoordinates) entityObj).coordinateIter();
                                while(coordIter.hasNext()){
                                    double[] coord = coordIter.next();
                                    pts.add(coord);
                                }
                            }
                        }
                    }
                }
            }

            //populate ptsToCluster
            int nPts = pts.size();
            if(nPts > 0){
                double[][] allPts = new double[nPts][];
                for (int i = 0; i < nPts; i++) {
                    double[] pt = pts.get(i);
                    allPts[i] = pt;
                }
                ptsToCluster.set(allPts);
            }

            //we have some points and some selected entities
            final int nSelectedEntities = selectedEntities;
            if(nPts > 0 && selectedEntities > 0){
                Platform.runLater(()->{
                    getSelectedEntitiesLabel().setText(nSelectedEntities + " selected entities");
                    getClusterButton().setDisable(false);
                });
            }else{
                Platform.runLater(()->{
                    getSelectedEntitiesLabel().setText(nSelectedEntities + " selected entities");
                    getClusterButton().setDisable(true);
                });
            }

        };

        CompletableFuture.runAsync(task, exec).handle((aVoid, throwable) -> {
            updatingView.set(false);
            return null;
        });

    }

    protected void doClustering(){

        final double[][] pts = AbstractClusterViewController.this.ptsToCluster.get();

        if(pts != null){

            setSpinnersDisabled(true);

            // get rid of disable
            getProgressBar().setDisable(false);
            getProgressBar().setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
            getClusterButton().setDisable(true);

            ThreadFactory threadFactory = r -> new Thread(r, "Clustering");

            ExecutorService exec = Executors.newSingleThreadExecutor(threadFactory);

            Runnable task  = () -> {
                final boolean success = doClusteringImpl(pts);
                Platform.runLater(()->{
                    getProgressBar().setProgress(0);
                    getClusterButton().setDisable(false);
                    setSpinnersDisabled(false);
                    if(success){
                        Stage stage = (Stage) getClusterButton().getScene().getWindow();
                        stage.close();
                    }
                });
                exec.shutdown();
            };

            CompletableFuture.runAsync(task, exec);
        }
    }


    protected boolean isShowing() {
        Button button = getClusterButton();
        if (button == null) {
            return false;
        }
        Scene scene = button.getScene();
        if (scene == null) {
            return false;
        }
        Stage stage = (Stage) scene.getWindow();
        return stage != null && stage.isShowing();
    }

    protected abstract void setSpinnersDisabled(boolean disabled);
    protected abstract boolean doClusteringImpl(double[][] pts);
    protected abstract ProgressBar getProgressBar();
    protected abstract Label getSelectedEntitiesLabel();
    protected abstract Button getClusterButton();


}
