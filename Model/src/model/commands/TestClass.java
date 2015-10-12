package model.commands;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;


public class TestClass {
    public static void main(String args[]) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        CommandModel model = new CommandModel("d:\\Programms\\TestFiles\\root");
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
