package moe.plushie.armourers_workshop.library.data;

import moe.plushie.armourers_workshop.init.common.AWCore;

public abstract class SkinLibraryManager {

    private static SkinLibraryManager RUNNING;

    public static SkinLibraryManager getInstance() {
        return RUNNING;
    }

    public void run() {
        RUNNING = this;
    }

    public static class Client extends SkinLibraryManager {

        final SkinLibrary localSkinLibrary = new SkinLibrary(AWCore.getSkinLibraryDirectory());

        public Client() {
        }

        @Override
        public void run() {
            super.run();
            this.localSkinLibrary.reload();
        }

        public SkinLibrary getLocalSkinLibrary() {
            return localSkinLibrary;
        }

        //        protected final SkinLibrary localSkinLibrary;
//        protected final SkinLibrary publicSkinLibrary;
//        protected final SkinLibrary privateSkinLibrary;
//
//        this.localSkinLibrary = new SkinLibrary();
//        this.publicSkinLibrary = new SkinLibrary();
//        this.privateSkinLibrary = new SkinLibrary();

        // recive(filelist) => public/private

    }

    public static class Server extends SkinLibraryManager {

        // load all and sync to all client

        public Server() {

        }

        @Override
        public void run() {
            super.run();
        }
    }
}
