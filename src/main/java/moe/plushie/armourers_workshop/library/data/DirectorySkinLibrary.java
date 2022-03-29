//package moe.plushie.armourers_workshop.library.data;
//
//import moe.plushie.armourers_workshop.api.skin.ISkinLibrary;
//import moe.plushie.armourers_workshop.api.skin.ISkinType;
//import org.apache.commons.io.FilenameUtils;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//
//public class DirectorySkinLibrary implements ISkinLibrary {
//
//    protected File directory;
//    protected File rootDirectory;
//
//    protected ArrayList<ISkinLibrary.Entry> entries = new ArrayList<>();
//
//    public DirectorySkinLibrary(File file) {
//        this.directory = file;
//        this.rootDirectory = file;
//        this.loadEntries();
//    }
//
//    public void loadEntries() {
//        File[] files = directory.listFiles();
//        String rr = rootDirectory.getAbsolutePath();
//        ArrayList<Entry> entries = new ArrayList<>();
//        if (files != null) {
//            for (File file : files) {
//                if (file.isDirectory()) {
//                    entries.add(new Entry(file.getName(), file, rr));
//                } else if (file.getName().toLowerCase().endsWith(".armour")) {
//                    entries.add(new Entry(FilenameUtils.removeExtension(file.getName()), file, rr));
//                }
//            }
//            Collections.sort(entries);
//        }
//        if (!directory.equals(rootDirectory)) {
//            entries.add(0, new Entry("..", directory.getParentFile(), rr));
//        }
//        this.entries = new ArrayList<>(entries);
//
////        ArrayList<LibraryFile> fileList = new ArrayList<LibraryFile>();
////
////        if (!directory.exists() | !directory.isDirectory()) {
////            return fileList;
////        }
////
////        File libraryDir = ArmourersWorkshop.getProxy().getSkinLibraryDirectory();
////
////
////        File[] templateFiles;
////        try {
////            templateFiles = directory.listFiles();
////        } catch (Exception e) {
////            ModLogger.log(Level.ERROR, "Armour file list load failed.");
////            e.printStackTrace();
////            return fileList;
////        }
////
////        for (int i = 0; i < templateFiles.length; i++) {
////            if (templateFiles[i].getName().endsWith(".armour")) {
////                String cleanName = FilenameUtils.removeExtension(templateFiles[i].getName());
////                String path = templateFiles[i].getPath().replace(templateFiles[i].getName(), "");
////                path = path.replace(libraryDir.getPath(), "");
////                ISkinType skinType = SkinIOUtils.getSkinTypeNameFromFile(templateFiles[i]);
////                if (skinType != null) {
////                    fileList.add(new LibraryFile(cleanName, path, skinType));
////                }
////            }
////            else {
////                if (templateFiles[i].isDirectory()) {
////
////                    String name = templateFiles[i].getName();
////                    String path = templateFiles[i].getParent() + "/";
////                    path = path.replace(ArmourersWorkshop.getProxy().getSkinLibraryDirectory().getPath(), "");
////                    path = path.replace("\\", "/");
////
////                    if (!name.equals("private")) {
////                        fileList.add(new LibraryFile(templateFiles[i].getName(), path, null, true));
////                    }
////                }
////            }
////        }
////        Collections.sort(fileList);
////
////        if (subDirectories) {
////            for (int i = 0; i < templateFiles.length; i++) {
////                if (templateFiles[i].isDirectory()) {
////                    fileList.addAll(getSkinFilesInDirectory(templateFiles[i], true));
////                }
////            }
////        }
////
////
////        return fileList;
//    }
//
//    public String getDirectory() {
//        return directory.getAbsolutePath();
//    }
//
//    public void setDirectory(String directory) {
//        this.directory = new File(rootDirectory, directory);
//        this.loadEntries();
//    }
//
//    public Collection<ISkinLibrary.Entry> search(String name, ISkinType skinType) {
//        return entries;
////        return Collections.emptyList();
//    }
//
////    public static class Entry implements Comparable<Entry>, ISkinLibrary.Entry {
////
////        private final String name;
////        private final String fullName;
////
////        private final boolean isDirectory;
////        private final boolean isPrivateDirectory = false;
////
////        public Entry(String name, File file, String base) {
////            this.name = name;
////            this.fullName = file.getAbsolutePath().replace(base, "");
////            this.isDirectory = file.isDirectory();
////        }
////
////        @Override
////        public String getName() {
////            return name;
////        }
////
////        @Override
////        public String getAbsolutePath() {
////            return fullName;
////        }
////
////        @Override
////        public boolean isDirectory() {
////            return isDirectory;
////        }
////
////        @Override
////        public boolean isPrivateDirectory() {
////            return isPrivateDirectory;
////        }
////
////        @Override
////        public int compareTo(Entry o) {
////            if (isDirectory() & !o.isDirectory()) {
////                return fullName.compareToIgnoreCase(o.fullName) - 1000000;
////            } else if (!isDirectory() & o.isDirectory()) {
////                return fullName.compareToIgnoreCase(o.fullName) + 1000000;
////            }
////            return fullName.compareToIgnoreCase(o.fullName);
////        }
////    }
//}
//
//
