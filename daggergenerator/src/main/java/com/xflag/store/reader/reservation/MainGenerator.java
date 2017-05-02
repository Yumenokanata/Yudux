package indi.yume.demo.newapplication;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

import indi.yume.tools.codegenerator.template.VarStringEngine;
import indi.yume.demo.newapplication.generators.Schema;

public class MainGenerator {
    public static void main(String[] args){
        String prefix = "";
        File baseFile = new File("");
        String resPath = baseFile.getAbsoluteFile() + "/app/src/main";

        Scanner sc = new Scanner(System.in);

        if(prefix == null || "".equals(prefix)) {
            System.out.println("画面の名前は入力してください：(eg: A01_1_ChatTest -> A01_1_ChatTestFragment)");
            prefix = sc.nextLine();
        }

        System.out.println("生成： 1、Activity 2、Fragment：");
        int type = sc.nextInt();

        VarStringEngine varStringEngine = new VarStringEngine();
        varStringEngine.binding("basePackage", "indi.yume.demo.newapplication");
        varStringEngine.binding("diPackage", ".di");
        varStringEngine.binding("uiPackage", ".ui");
        varStringEngine.binding("fragmentPackage", ".ui.fragment");
        varStringEngine.binding("activityPackage", ".ui.activity");
        varStringEngine.binding("presenterPackage", ".ui.presenter");
        varStringEngine.binding("componentPackage", ".ui.component");
        varStringEngine.binding("modulePackage", ".ui.module");

        varStringEngine.binding("name", prefix);

        DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        varStringEngine.binding("note", "Created by DaggerGenerator on " + df.format(Calendar.getInstance().getTime()) + ".");

        varStringEngine.binding("type", "Fragment");
        try {
            Schema schema = null;
            if(type == 1) {
                schema = Schema.buildActivity(resPath, varStringEngine);
            } else {
                schema = Schema.buildFragment(resPath, varStringEngine);
            }

            schema.generatorFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
