package model.commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;


public class TestClass {
    public static void main(String args[]) throws IOException {

        CommandModel model = new CommandModel();
        model.Initialize("d:\\Programms\\TestFiles\\root");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String cmd = "";
        while(true) {
            try {
                cmd = reader.readLine();
                if(cmd.compareTo("break") == 0){
                    break;
                }
                System.out.println(model.runCommand(cmd).toString());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
