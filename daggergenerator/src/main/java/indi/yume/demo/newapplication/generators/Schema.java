package indi.yume.demo.newapplication.generators;

import java.io.File;

import indi.yume.tools.codegenerator.generator.ClazzGenerator;
import indi.yume.tools.codegenerator.template.TemplateEngine;
import indi.yume.tools.codegenerator.template.VarStringEngine;
import indi.yume.demo.newapplication.generators.util.FileUtil;

/**
 * Created by yume on 15/9/26.
 */
public class Schema {
    private static final int TYPE_FRAGMENT = 1;
    private static final int TYPE_ACTIVITY = 2;

    private String view;
    private String handler;
    private String state;
    private ClazzGenerator presenter;
    private ClazzGenerator component;
    private ClazzGenerator module;
    private String xmlFileContent;

    private String basePath;

    private VarStringEngine varStringEngine;

    private int type = TYPE_FRAGMENT;

    private Schema(){}

    public static Schema buildFragment(String basePath, VarStringEngine varStringEngine) throws Exception {
        Schema schema = new Schema();
        schema.basePath = basePath;
        schema.type = TYPE_FRAGMENT;

        File baseFile = new File("");
        String BaseUri = baseFile.getAbsoluteFile() + "/daggergenerator/src/main/res";
        TemplateEngine templateEngine = new TemplateEngine(new File(BaseUri + "/config.xml"), varStringEngine);

        schema.view = varStringEngine.analysisString(FileUtil.readFile(new File(BaseUri + "/fragment/fragment.txt")));
        schema.handler = varStringEngine.analysisString(FileUtil.readFile(new File(BaseUri + "/fragment/handler.txt")));
        schema.state = varStringEngine.analysisString(FileUtil.readFile(new File(BaseUri + "/fragment/state.txt")));
        schema.presenter = templateEngine.setTemplateFile(new File(BaseUri + "/fragment/presenter.xml"));
        schema.component = templateEngine.setTemplateFile(new File(BaseUri + "/fragment/component.xml"));
        schema.module = templateEngine.setTemplateFile(new File(BaseUri + "/fragment/module.xml"));
        schema.xmlFileContent = varStringEngine.analysisString(FileUtil.readFile(new File(BaseUri + "/fragment/fragment_layoutxml.xml")));

        schema.varStringEngine = varStringEngine;

        return schema;
    }

    public static Schema buildActivity(String basePath, VarStringEngine varStringEngine) throws Exception {
        Schema schema = new Schema();
        schema.basePath = basePath;
        schema.type = TYPE_ACTIVITY;

        File baseFile = new File("");
        String BaseUri = baseFile.getAbsoluteFile() + "/daggergenerator/src/main/res";
        TemplateEngine templateEngine = new TemplateEngine(new File(BaseUri + "/config.xml"), varStringEngine);

        schema.view = varStringEngine.analysisString(FileUtil.readFile(new File(BaseUri + "/activity/activity.txt")));
        schema.handler = varStringEngine.analysisString(FileUtil.readFile(new File(BaseUri + "/activity/handler.txt")));
        schema.state = varStringEngine.analysisString(FileUtil.readFile(new File(BaseUri + "/activity/state.txt")));
        schema.presenter = templateEngine.setTemplateFile(new File(BaseUri + "/activity/presenter.xml"));
        schema.component = templateEngine.setTemplateFile(new File(BaseUri + "/activity/component.xml"));
        schema.module = templateEngine.setTemplateFile(new File(BaseUri + "/activity/module.xml"));
        schema.xmlFileContent = varStringEngine.analysisString(FileUtil.readFile(new File(BaseUri + "/activity/activity_layoutxml.xml")));

        schema.varStringEngine = varStringEngine;

        return schema;
    }

    public void generatorFiles() throws Exception {
        switch (type) {
            case TYPE_FRAGMENT:
                generatorFragment();
                break;
            case TYPE_ACTIVITY:
                generatorActivity();
                break;
        }
    }

    private void generatorFragment() throws Exception {
        File viewFile = FileUtil.newFile(basePath,
                "java",
                varStringEngine.analysisString("${basePackage}${fragmentPackage}.${-name}").replace(".", File.separator),
                varStringEngine.analysisString("${name}Fragment.java"));
        File xmlFile = FileUtil.newFile(basePath,
                "res",
                "layout",
                varStringEngine.analysisString("${_-name}_fragment.xml"));

        File handlerFile = FileUtil.newFile(basePath,
                "java",
                varStringEngine.analysisString("${basePackage}${fragmentPackage}.${-name}").replace(".", File.separator),
                varStringEngine.analysisString("${name}Handler.java"));
        File stateFile = FileUtil.newFile(basePath,
                "java",
                varStringEngine.analysisString("${basePackage}${fragmentPackage}.${-name}").replace(".", File.separator),
                varStringEngine.analysisString("${name}State.java"));

        generatorFiles(viewFile, handlerFile, stateFile, xmlFile);
    }

    private void generatorActivity() throws Exception {
        File viewFile = FileUtil.newFile(basePath,
                "java",
                varStringEngine.analysisString("${basePackage}${activityPackage}.${-name}").replace(".", File.separator),
                varStringEngine.analysisString("${name}Activity.java"));
        File xmlFile = FileUtil.newFile(basePath,
                "res",
                "layout",
                varStringEngine.analysisString("${_-name}_activity.xml"));

        File handlerFile = FileUtil.newFile(basePath,
                "java",
                varStringEngine.analysisString("${basePackage}${activityPackage}.${-name}").replace(".", File.separator),
                varStringEngine.analysisString("${name}Handler.java"));
        File stateFile = FileUtil.newFile(basePath,
                "java",
                varStringEngine.analysisString("${basePackage}${activityPackage}.${-name}").replace(".", File.separator),
                varStringEngine.analysisString("${name}State.java"));

        generatorFiles(viewFile, handlerFile, stateFile, xmlFile);

//        File androidManifest = FileUtil.newFile(basePath, "AndroidManifest.xml");
//        String manifestString = FileUtil.readFile(androidManifest);
//        manifestString.indexOf("")

        System.out.println("Please add Activity Code to AndroidManifest.xml");
    }

    private void generatorFiles(File viewFile, File handlerFile, File stateFile, File xmlFile) throws Exception {

        File presenterFile = FileUtil.newFile(basePath,
                "java",
                varStringEngine.analysisString("${basePackage}${presenterPackage}").replace(".", File.separator),
                varStringEngine.analysisString("${name}Presenter.java"));
        File componentFile = FileUtil.newFile(basePath,
                "java",
                varStringEngine.analysisString("${basePackage}${componentPackage}").replace(".", File.separator),
                varStringEngine.analysisString("${name}Component.java"));
        File moduleFile = FileUtil.newFile(basePath,
                "java",
                varStringEngine.analysisString("${basePackage}${modulePackage}").replace(".", File.separator),
                varStringEngine.analysisString("${name}Module.java"));

        if(viewFile.exists())
            throw new Error("File " + viewFile.getAbsolutePath() + " is exists");
        if(handlerFile.exists())
            throw new Error("File " + handlerFile.getAbsolutePath() + " is exists");
        if(stateFile.exists())
            throw new Error("File " + stateFile.getAbsolutePath() + " is exists");
        if(presenterFile.exists())
            throw new Error("File " + presenterFile.getAbsolutePath() + " is exists");
        if(componentFile.exists())
            throw new Error("File " + componentFile.getAbsolutePath() + " is exists");
        if(moduleFile.exists())
            throw new Error("File " + moduleFile.getAbsolutePath() + " is exists");
        if(xmlFile.exists())
            throw new Error("File " + xmlFile.getAbsolutePath() + " is exists");

        FileUtil.writeToFile(view, viewFile);
        System.out.println("Generate file: " + viewFile.getAbsolutePath());
        FileUtil.writeToFile(handler, handlerFile);
        System.out.println("Generate file: " + handlerFile.getAbsolutePath());
        FileUtil.writeToFile(state, stateFile);
        System.out.println("Generate file: " + stateFile.getAbsolutePath());
        FileUtil.writeToFile(presenter.render(), presenterFile);
        System.out.println("Generate file: " + presenterFile.getAbsolutePath());
        FileUtil.writeToFile(component.render(), componentFile);
        System.out.println("Generate file: " + componentFile.getAbsolutePath());
        FileUtil.writeToFile(module.render(), moduleFile);
        System.out.println("Generate file: " + moduleFile.getAbsolutePath());

        FileUtil.writeToFile(xmlFileContent, xmlFile);
        System.out.println("Generate file: " + xmlFile.getAbsolutePath());
    }
}
