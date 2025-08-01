package net.ygbstudio.postdirector.core;

import java.util.*;

public final class Helpers implements Util {

    private Helpers() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    public static void accessUrlJsoup(String url){
        // It returns the jSoup object
        Util.unsupportedUtil();
    }

    public static void accessUrl(String url){
        // It returns a string
        Util.unsupportedUtil();
    }

    public static void cleanFilename(String filename, String extension){
        Util.unsupportedUtil();
    }

    public static void cleanFileCache(Object cacheFolder, String fileExtension) {
        // cacheFolder must be either String or Path
        Util.unsupportedUtil();
    }

    public static void fetchDataSQL(String sqlQuery, Object sqlCursor){
        // Returns rowset
        Util.unsupportedUtil();
    }

    public static void fileCreationHelper(ArrayList<String> suggestions, String extension){
        // Returns filename either suggested or validated from user input
        Util.unsupportedUtil();
    }

    public static void filenameSelect(String extension, boolean parent, String folder){
        // Returns file name without relative path.
        Util.unsupportedUtil();
    }

    public static void exportJSONFile(String filename, Object info, int indent, boolean parent, String targetDir){
        Util.unsupportedUtil();
    }

    public static void exportMapToCSV(Collection<?> info, String filename){
        Util.unsupportedUtil();
    }

    public static void getWebDriver(String downloadFolder, boolean headless, boolean gecko, boolean noImages){
        // Return Selenium Webdriver
        Util.unsupportedUtil();
    }

    public static void loadFilePath(String filename){
        // Returns file path
        Util.unsupportedUtil();
    }

    public static void loadLocalJSON(Object filenameOrPath, boolean logErr){
        Util.unsupportedUtil();
    }

    public static void removeIfPresent(String filename){
        Util.unsupportedUtil();
    }

    public static void searchFilesByExt(String extension, Object folderOrPath, boolean recursive, boolean parent, boolean absPaths){
        Util.unsupportedUtil();
    }

    public static void writeToFile(String filename, String folder, String extension, Object info){
        Util.unsupportedUtil();
    }

    public static void writePropertyFile(String filename, String key, String value){
        Util.unsupportedUtil();
    }

    public static void StringEncodeB64(String encodeStr){
        Util.unsupportedUtil();
    }

    public static void generateSHA256Hash(String unhashedStr){
        Util.unsupportedUtil();
    }

    public static void generateRandStr(int kSample){
        Util.unsupportedUtil();
    }

    public static void splitChar(Optional<String> splitStr, String placeholder, boolean charList, boolean charListRaw){
        Util.unsupportedUtil();
    }

    public static void clearConsole(){
        Util.unsupportedUtil();
    }

    public static void filterApostrophe(String toFilterStr){
        Util.unsupportedUtil();
    }

    public static void walkToProjectRootDir(String projectRootDir, String sourcePath){
        Util.unsupportedUtil();
    }

    public static void propertyParser(String configFilename, String targetHint){
        Util.unsupportedUtil();
    }

    public static void loggingSetup(String loggingDir, String pathToThis){
        Util.unsupportedUtil();
    }

    public static void createStore(String storePath){
        Util.unsupportedUtil();
    }

    public static void applyOSPermissions(String filePath, boolean dirPermissions, boolean readOnly){
        Util.unsupportedUtil();
    }
}
