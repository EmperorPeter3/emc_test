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
    Path rootParentPath;
    Path rootPath = Paths.get(System.getProperty("user.dir")+"/root");
    int pathLength;
    JSONObject resultJSON;
    enum status{Error,Completed}
    /**
     * Initialize model with root path
     * @param path root path
     */
    CommandModel(String path) {
        rootPath = Paths.get(path);
        this.currentPath = rootPath;
        this.rootParentPath = rootPath.getParent();
        this.pathLength = this.rootParentPath.getNameCount();
    }

    /**
     * Chooses right method, that depends on input
     * @param command Command to execute in server
     * @return Status operation and TextStatus operation
     * @throws IOException
     */
    public JSONObject runCommand(String command) throws IOException {
        JSONObject outputJSON = new JSONObject();
        String[] cm = command.split(" ");
        String cmd = cm.length > 0 ? cm[0] : command;
        String path = cm.length > 1 ? cm[1] : "";
        try {
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
        }
        catch(IOException e){
                outputJSON.put("Status", status.Error);
                outputJSON.put("StatusText", e.getMessage());
            }
        return outputJSON;
    }

    /**
     * Change directory
     * @param path Path to desirable directory
     * @return Status operation and TextStatus operation
     * @throws IOException
     */
    private JSONObject cdCommand(String path) throws IOException{
        resultJSON = new JSONObject();
        Path pt;
        pt = this.getAbsPath(path);
        if (Files.notExists(pt)) {
            throw new FileNotFoundException("Invalid path: directory not found.");
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
    private JSONObject mkdirCommand(String path) throws IOException {
        resultJSON = new JSONObject();
        Path pt;
        pt = this.getAbsPath(path);
        if (Files.exists(pt)) {
            throw new FileAlreadyExistsException("File already exists.");
        }
        Files.createDirectories(pt);
        resultJSON.put("Status", status.Completed);
        resultJSON.put("StatusText", "Directory successfully created.");
        return resultJSON;
    }

    /**
     * Returns list of files and directories in current position
     * @param path Path to directory, where execute a command
     * @return Status operation and TextStatus operation
     * @throws IOException
     */
    private JSONObject lsCommand(String path) throws IOException {
        resultJSON = new JSONObject();
        Path pt;
        JSONObject resultJSON = new JSONObject();
        pt = this.getAbsPath(path);
        if (Files.notExists(pt)) {
            throw new FileNotFoundException("Invalid path: directory not found.");
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
        return resultJSON;
    }

    /**
     * Create file
     * @param path Path to creating file
     * @return Status operation and TextStatus operation
     * @throws IOException
     */
    private JSONObject touchCommand(String path) throws IOException {
        resultJSON = new JSONObject();
        Path pt;
        pt = this.getAbsPath(path);
        Files.createFile(pt);
        resultJSON.put("Status", status.Completed);
        resultJSON.put("StatusText", "File successfully created.");
        return resultJSON;
    }

    /**
     * Path with result of command
     * @param path
     * @return String output path.
     */
    private String outputString(Path path){
        return this.rootParentPath.relativize(path).toString();
    }

    /**
     * Get absolute path for input path and throws exception, if path is illegal
     * @param path
     * @return Absolute path compared to root path.
     * @throws IOException
     */
    private Path getAbsPath(String path) throws IOException{
        Path pt = this.currentPath.resolve(Paths.get(path)).normalize();
        Path root = pt.getRoot();
        Path root2 = rootPath.getRoot();
        if(!root2.equals(root)){
            throw new IOException("Path is invalid");
        }
        if(pt.subpath(0,pathLength).equals(rootPath.subpath(0,pathLength))){
        return pt;
        }
        throw new IOException("Path is invalid");
    }
}
