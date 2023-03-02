package moe.plushie.armourers_workshop.library.data;

//public class SkinLibraryMonitor {
//
//    private final int interval;
//    private final File rootPath;
//    private final Delegate delegate;
//
//    private FileAlterationMonitor monitor;
//
//    public SkinLibraryMonitor(SkinLibrary library, File rootPath, int interval) {
//        this.delegate = new Delegate(library);
//        this.rootPath = rootPath;
//        this.interval = interval;
//    }
//
//    public void start() {
//        if (monitor != null) {
//            return;
//        }
//        Delegate delegate = this.delegate;
//        FileFilter filter = f -> f.isDirectory() || f.getName().endsWith(Constants.EXT);
//        FileAlterationObserver observer = new FileAlterationObserver(rootPath, filter);
//        observer.addListener(new FileAlterationListenerAdaptor() {
//
//            @Override
//            public void onDirectoryCreate(File directory) {
//                delegate.didChange();
//            }
//
//            @Override
//            public void onDirectoryChange(File directory) {
//                delegate.didChange();
//            }
//
//            @Override
//            public void onDirectoryDelete(File directory) {
//                delegate.didChange();
//            }
//
//            @Override
//            public void onFileCreate(File file) {
//                delegate.didChange();
//            }
//
//            @Override
//            public void onFileChange(File file) {
//                delegate.didChange();
//            }
//
//            @Override
//            public void onFileDelete(File file) {
//                delegate.didChange();
//            }
//        });
//        try {
//            monitor = new FileAlterationMonitor(interval, observer);
//            monitor.start();
//        } catch (Exception exception) {
//            exception.printStackTrace();
//        }
//    }
//
//    public void stop() {
//        if (monitor == null) {
//            return;
//        }
//        try {
//            monitor.stop();
//            monitor = null;
//        } catch (Exception exception) {
//            exception.printStackTrace();
//        }
//    }
//
//    protected static class Delegate {
//
//        private WeakReference<SkinLibrary> library;
//
//        protected Delegate(SkinLibrary library) {
//            this.library = new WeakReference<>(library);
//        }
//
//        protected void didChange() {
//            SkinLibrary library = this.library.get();
//            if (library != null) {
//                library.reload();
//            }
//        }
//    }
//
//}
