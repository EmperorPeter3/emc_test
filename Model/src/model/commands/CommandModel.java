package model.commands;
/*
  methods, that works with files.
*/

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class CommandModel {
    Path currentPath;
    Path rootParentPath = Paths.get(System.getProperty("user.dir")+"/root");
    int pathLength;
    JSONObject resultJSON;

    CommandModel(){
        this.currentPath = rootParentPath;
        this.pathLength = this.rootParentPath.getNameCount();
    }
    enum status{Error,Completed}
    /**
     * Initialize model with root path
     * @param path
     */
    void Initialize(String path){
        this.rootParentPath = Paths.get(path).getParent();
        this.currentPath = Paths.get(path);
        this.pathLength = this.rootParentPath.getNameCount();
    }

    /**
     * Create root parent path in server directory
     * @throws IOException
     */
    public void createRootParentPath() throws IOException
    {
        Path pt=rootParentPath;
        Files.createDirectory(pt);
        this.currentPath = rootParentPath;
        this.pathLength = this.rootParentPath.getNameCount();
    }

    /**
     * Delete root parent path in server directory
     * @throws IOException
     */
    public void removeRootParentPath() throws IOException
    {
        Path pt;
        pt=rootParentPath;
        Files.delete(pt);
    }

    /**
     * Chooses right method, that depends on input
     * @param comand Comand for doing in server
     * @return Status operation and TextStatus operation
     * @throws IOException
     */
    public JSONObject runCommand(String comand) throws IOException {
        JSONObject outputJSON;
        String[] cm = comand.split(" ");
        String cmd = cm.length > 0 ? cm[0] : comand;
        String path = cm.length > 1 ? cm[1] : "";
        switch (cmd) {
            case "cd":
                outputJSON = cdCommand(path);
                break;
            case "mkdir":
                outputJSON = mkdirCommand(path);
                break;
            case "ls":
                outputJSON = lsCommand(path);
                break;
            case "touch":
                outputJSON = touchCommand(path);
                break;
            default:
                throw new IOException(cmd + " is not recognised as a command.");
        }
        return outputJSON;
    }

    /**
     * Change directory
     * @param path Path to change directory
     * @return Status operation and TextStatus operation
     * @throws IOException
     */
    JSONObject cdCommand(String path) throws IOException{
        resultJSON = new JSONObject();
        Path pt;
        pt = this.getAbsPath(path);
        if (Files.notExists(pt)) {
            throw new FileNotFoundException();
        }
        if (!Files.isDirectory(pt)) {
            throw new IOException("The directory name is invalid.");
        }
        switch (path) {
            case "":
                break;
            case "..":
                this.currentPath = currentPath.getParent();
                break;
            default:
                this.currentPath = pt;
        }
        resultJSON.put("Status", status.Completed);
        resultJSON.put("StatusText", outputString(currentPath));
        return resultJSON;
    }

    /**
     * Create directory/directories
     * @param path Status operation and TextStatus operation
     * @return Status operation and TextStatus operation
     * @throws IOException
     */
    JSONObject mkdirCommand(String path) throws IOException {
        resultJSON = new JSONObject();
        Path pt;
        pt = this.getAbsPath(path);
        if (Files.exists(pt)) {
            throw new FileAlreadyExistsException(path);
        }
        Files.createDirectories(pt);
        resultJSON.put("Status", status.Completed);
        //resultJSON.put("StatusText", "Directory " + path + " created.");
        resultJSON.put("StatusText", "");
        return resultJSON;
    }

    /**
     * Returns list of files and directories in current position
     * @param path Path to directory
     * @return Status operation and TextStatus operation
     * @throws IOException
     */
    JSONObject lsCommand(String path) throws IOException {
        resultJSON = new JSONObject();
        Path pt;
        JSONObject resultJSON = new JSONObject();
        pt = this.getAbsPath(path);
        if (Files.notExists(pt)) {
            throw new FileNotFoundException();
        }
        if (!Files.isDirectory(pt)) {
            throw new IOException("The directory name is invalid.");
        }
        JSONArray directoriesList = new JSONArray();
        JSONArray filesList = new JSONArray();
        DirectoryStream<Path> stream = Files.newDirectoryStream(pt);
        for (Path file : stream) {
            if (file.toFile().isDirectory()) {
                directoriesList.add(file.getFileName().toString());
            } else {
                filesList.add(file.getFileName().toString());
            }
        }
        resultJSON.put("Status",status.Completed);
        resultJSON.put("directoriesList", directoriesList);
        resultJSON.put("filesList", filesList);
        resultJSON.put("StatusTextError", "");
        return resultJSON;
    }

    /**
     * Create file
     * @param path Path to creating file
     * @return Status operation and TextStatus operation
     * @throws IOException
     */
    JSONObject touchCommand(String path) throws IOException {
        resultJSON = new JSONObject();
        Path pt;
        pt = this.getAbsPath(path);
        Files.createFile(pt);
        resultJSON.put("Status", status.Completed);
        //resultJSON.put("StatusText", "File " + path + " created.");
        resultJSON.put("StatusText", "");
        return resultJSON;
    }

    /**
     * Path with result of command
     * @param path
     * @return
     */
    private String outputString(Path path){
        return this.rootParentPath.relativize(path).toString();
    }

    /**
     * Get absolute path for input path and throws exception, if path is illegal
     * @param path
     * @return
     * @throws IOException
     */
    private Path getAbsPath(String path) throws IOException{
        Path pt = this.currentPath.resolve(Paths.get(path)).normalize();
        Path rtpt = this.rootParentPath.resolve("root");
        int a = rtpt.compareTo(pt);
        if(a<=0){
        return pt;}
        throw new IOException("Path is invalid");
    }
}
