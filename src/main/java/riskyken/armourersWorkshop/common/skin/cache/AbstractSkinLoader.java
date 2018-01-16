package riskyken.armourersWorkshop.common.skin.cache;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;

import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinIdentifier.SkinIdentifierType;

public abstract class AbstractSkinLoader<T> {
    
    private final SkinIdentifierType identifierType;
    private final ArrayList<T> skinLoadQueue;
    
    private final Executor executorSkinLoader;
    private CompletionService<Skin> completionServiceSkinLoader;
    
    public AbstractSkinLoader(SkinIdentifierType identifierType) {
        this.identifierType = identifierType;
        skinLoadQueue = new ArrayList<T>();
        executorSkinLoader = Executors.newFixedThreadPool(1);
        completionServiceSkinLoader = new ExecutorCompletionService<Skin>(executorSkinLoader);
    }
    
    public void softloadSkin(T identifier) {
        completionServiceSkinLoader.submit(new SkinLoader<T>(identifier));
    }
    
    public Skin loadSkin(T identifier) {
        return null;
    }
    
    public abstract Skin onLoadedSkin(T identifier);
    
    private static class SkinLoader<T> implements Callable<Skin> {

        public SkinLoader(T identifier) {
            // TODO Auto-generated constructor stub
        }
        
        @Override
        public Skin call() throws Exception {
            // TODO Auto-generated method stub
            return null;
        }
        
    }

    public interface SkinLoaderCallback<T> {
        
         public void onSkinLoad(Skin skin, T identifier, SkinIdentifierType identifierType);
    }
}
