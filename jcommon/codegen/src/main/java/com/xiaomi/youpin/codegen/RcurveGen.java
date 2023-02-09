/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.codegen;

import com.xiaomi.youpin.codegen.common.FileUtils;
import com.xiaomi.youpin.codegen.generator.ClassGenerator;
import com.xiaomi.youpin.codegen.generator.DirectoryGenerator;
import com.xiaomi.youpin.codegen.generator.FileGenerator;
import com.xiaomi.youpin.codegen.generator.PomGenerator;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.xiaomi.youpin.codegen.bo.Constants.DUBBO_THREE;
import static com.xiaomi.youpin.codegen.bo.Constants.DUBBO_TWO;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class RcurveGen {

    public Result<String> generateAndZip(String projectPath, String projectName, String groupId, String packageName, String author, String versionId, boolean needTomCat, int dubboType) {
        return generateAndZip(projectPath, projectName, groupId, packageName, author, versionId, needTomCat, "", "", dubboType);
    }

    public Result<String> generateAndZip(String projectPath, String projectName, String groupId, String packageName, String author, String versionId, boolean needTomCat) {
        return generateAndZip(projectPath, projectName, groupId, packageName, author, versionId, needTomCat, "", "", DUBBO_TWO);
    }

    public Result<String> generateAndZip(String projectPath, String projectName, String groupId, String packageName, String author, String versionId, boolean needTomCat, String talosKey, String talosSecret, int dubboType) {

        String srcPath = "/src/main/java/";
        String testPath = "/src/test/java/";
        String deployPath = "/deploy/manifests";

        String packagePath = packageName.replaceAll("\\.", "/");

        try {
            //创建项目
            DirectoryGenerator directoryGenerator = new DirectoryGenerator(projectPath, projectName, "");
            directoryGenerator.generator();
            generageParentPom(projectPath, projectName, groupId, versionId);
            generageReadMe(projectPath, projectName);
            generageGitignore(projectPath, projectName);

            //创建module: api
            DirectoryGenerator apiModule = new DirectoryGenerator(projectPath, projectName, projectName + "-api" +
                    File.separator + srcPath + File.separator + packagePath + File.separator + "api" + File.separator + "service");
            apiModule.generator();
            generateApiPom(projectPath, projectName, groupId, versionId, dubboType);
            generateDubboApi(projectPath, projectName, packageName, packagePath + File.separator + "api" + File.separator + "service", projectName + "-api" + File.separator + srcPath, dubboType);
            DirectoryGenerator apiTest = new DirectoryGenerator(projectPath, projectName, projectName + "-api" +
                    File.separator + testPath + File.separator + packagePath + File.separator + "test");
            apiTest.generator();

            //创建module: common
            DirectoryGenerator commonModule = new DirectoryGenerator(projectPath, projectName, projectName + "-common"
                    + File.separator + srcPath
            );
            commonModule.generator();
            generateCommonPom(projectPath, projectName, groupId, versionId, dubboType);
            DirectoryGenerator commonTest = new DirectoryGenerator(projectPath, projectName, projectName + "-common" +
                    File.separator + testPath);
            commonTest.generator();

            //创建module: service
            DirectoryGenerator serviceModule = new DirectoryGenerator(projectPath, projectName, projectName + "-service" +
                    File.separator + srcPath + File.separator + packagePath);
            serviceModule.generator();
            DirectoryGenerator serviceModule1 = new DirectoryGenerator(projectPath, projectName, projectName + "-service" +
                    File.separator + srcPath + File.separator + packagePath + File.separator + "service");
            serviceModule1.generator();
            generateServicePom(projectPath, projectName, groupId, author, versionId, needTomCat, dubboType);
            DirectoryGenerator serviceTest = new DirectoryGenerator(projectPath, projectName, projectName + "-service" +
                    File.separator + testPath + File.separator + packagePath);
            serviceTest.generator();

            //创建module: server
            DirectoryGenerator serverModule = new DirectoryGenerator(projectPath, projectName, projectName + "-server" +
                    File.separator + srcPath + File.separator + packagePath + File.separator + "bootstrap");
            serverModule.generator();
            DirectoryGenerator serverModule1 = new DirectoryGenerator(projectPath, projectName, projectName + "-server" +
                    File.separator + srcPath + File.separator + packagePath + File.separator + "config");
            serverModule1.generator();
            generateServerPom(projectPath, projectName, packageName, groupId, author, versionId, dubboType);
            DirectoryGenerator serverTest = new DirectoryGenerator(projectPath, projectName, projectName + "-server" +
                    File.separator + testPath + File.separator + packagePath);
            serverTest.generator();
            //生成入口类
            generateBootstrap(projectPath, projectName, packageName, author, packagePath + File.separator + "bootstrap", projectName + "-server" + File.separator + srcPath);
            //生成配置文件
            generateResources(projectPath, projectName, versionId, talosKey, talosSecret);
            generateLogback(projectPath, projectName);

            generateDubboApiImp(projectPath, projectName, packageName, packagePath + File.separator + "service", projectName + "-service" + File.separator + srcPath, projectName);

            FileUtils.compress(projectPath + File.separator + projectName, projectPath + File.separator + projectName + ".zip");
        } catch (Exception e) {
            log.error("SpringBootProGen failed ", e);
            return Result.fail(GeneralCodes.InternalError, "InternalError");
        }

        return Result.success(projectPath + File.separator + projectName + ".zip");
    }

    private void generateResources(String projectPath, String projectName, String versionId, String talosKey, String talosSecret) {
        //生成文件夹
        DirectoryGenerator directoryGenerator = new DirectoryGenerator(projectPath, projectName, projectName + "-server/src/main/resources/config");
        directoryGenerator.generator();

        //生成文件
        FileGenerator fileGenerator = new FileGenerator(projectPath, projectName, projectName + "-server/src/main/resources/config.properties", "rcurve/application_properties.tml");
        FileGenerator devGenerator = new FileGenerator(projectPath, projectName, projectName + "-server/src/main/resources/config/dev.properties", "springboot_application_properties_dev.tml");
        FileGenerator stagingGenerator = new FileGenerator(projectPath, projectName, projectName + "-server/src/main/resources/config/staging.properties", "springboot_application_properties_st.tml");
        FileGenerator c3Generator = new FileGenerator(projectPath, projectName, projectName + "-server/src/main/resources/config/c3.properties", "springboot_application_properties_c3.tml");
        FileGenerator c4Generator = new FileGenerator(projectPath, projectName, projectName + "-server/src/main/resources/config/c4.properties", "springboot_application_properties_c4.tml");
        FileGenerator previewGenerator = new FileGenerator(projectPath, projectName, projectName + "-server/src/main/resources/config/preview.properties", "springboot_application_properties_preview.tml");
        FileGenerator dockerFileGenerator = new FileGenerator(projectPath, projectName, projectName + "-server/src/main/resources/Dockerfile", "springboot_docker_file.tml");


        Map<String, Object> m = new HashMap<>(5);
        m.put("appName", projectName);
        m.put("project", projectName);
        m.put("version", versionId + "-SNAPSHOT");
        m.put("talosKey", talosKey);
        m.put("talosSecret", talosSecret);
        fileGenerator.generator(m);
        devGenerator.generator(m);
        stagingGenerator.generator(m);
        c3Generator.generator(m);
        c4Generator.generator(m);
        previewGenerator.generator(m);
        dockerFileGenerator.generator(m);
    }

    private void generateLogback(String projectPath, String projectName) {
        //生成文件夹
        DirectoryGenerator directoryGenerator = new DirectoryGenerator(projectPath, projectName, projectName + "-server/src/main/resources");
        directoryGenerator.generator();

        //生成文件
        FileGenerator fileGenerator = new FileGenerator(projectPath, projectName, projectName + "-server/src/main/resources/logback.xml", "rcurve/logback.tml");
        Map<String, Object> m = new HashMap<>(1);
        m.put("project", projectName);
        fileGenerator.generator(m);
    }


    private void generateBootstrap(String projectPath, String projectName, String packageName, String author, String packagePath, String serviceSrcPath) {
        String templateName = "rcurve/bootstrap_class.tml";
        ClassGenerator classGenerator = new ClassGenerator(projectPath, projectName, serviceSrcPath, packagePath, this.adapterProjectNameToCamelName(projectName) + "Bootstrap", templateName);
        Map<String, Object> m = new HashMap<>(3);
        m.put("package", packageName);
        m.put("author", author);
        m.put("project", this.adapterProjectNameToCamelName(projectName));
        classGenerator.generator(m);
    }


    private void generateDubboApi(String projectPath, String projectName, String packageName, String packagePath, String serviceSrcPath, int dubboVersion) {
        String tmpName = "rcurve/dubbo_api_class.tml";
        ClassGenerator classGenerator = new ClassGenerator(projectPath, projectName, serviceSrcPath, packagePath, "DubboHealthService", tmpName);
        Map<String, Object> m = new HashMap<>(2);
        m.put("package", packageName);
        m.put("project", projectName);
        classGenerator.generator(m);
    }

    private void generateDubboApiImp(String projectPath, String projectName, String packageName, String packagePath, String serviceSrcPath, String appName) {
        String tmpName = "rcurve/dubbo_api_imp_class.tml";
        ClassGenerator classGenerator = new ClassGenerator(projectPath, projectName, serviceSrcPath, packagePath, "DubboHealthServiceImpl", tmpName);
        Map<String, Object> m = new HashMap<>(1);
        m.put("package", packageName);
        m.put("appName", appName);
        classGenerator.generator(m);
    }


    private void generateApiPom(String projectPath, String projectName, String groupId, String versionId, int dubboVersion) {
        String tmpName = "rcurve/pom_api.tml";
        //生成api module 下的pom文件
        PomGenerator apiPomGenerator = new PomGenerator(projectPath, projectName + File.separator + projectName + "-api", tmpName);
        Map<String, Object> ampom = new HashMap<>();
        ampom.put("groupId", groupId);
        ampom.put("parent_artifactId", projectName);
        ampom.put("artifactId", projectName + "-api");
        ampom.put("version", versionId + "-SNAPSHOT");
        ampom.put("version_id", versionId);
        apiPomGenerator.generator(ampom);
    }

    private void generateCommonPom(String projectPath, String projectName, String groupId, String versionId, int dubboVersion) {
        String tmlName = "rcurve/pom_common.tml";
        //生成api module 下的pom文件
        PomGenerator commonPomGenerator = new PomGenerator(projectPath, projectName + File.separator + projectName + "-common", tmlName);
        Map<String, Object> cmpom = new HashMap<>();
        cmpom.put("groupId", groupId);
        cmpom.put("parent_artifactId", projectName);
        cmpom.put("artifactId", projectName + "-common");
        cmpom.put("version", versionId + "-SNAPSHOT");
        commonPomGenerator.generator(cmpom);
    }

    private void generageParentPom(String projectPath, String projectName, String groupId, String versionId) {
        //生成主项目的pom文件
        PomGenerator pomGenerator = new PomGenerator(projectPath, projectName, "springboot_pom.tml");
        Map<String, Object> mpom = new HashMap<>();
        mpom.put("groupId", groupId);
        mpom.put("artifactId", projectName);
        mpom.put("version", versionId + "-SNAPSHOT");
        mpom.put("api_module", projectName + "-api");
        mpom.put("common_module", projectName + "-common");
        mpom.put("service_module", projectName + "-service");
        mpom.put("server_module", projectName + "-server");
        pomGenerator.generator(mpom);
    }

    private void generageReadMe(String projectPath, String projectName) {
        FileGenerator mdGenerator = new FileGenerator(projectPath, projectName, "README.md", "springboot_readme.tml");
        mdGenerator.generator(new HashMap<>());
    }

    private void generageGitignore(String projectPath, String projectName) {
        FileGenerator mdGenerator = new FileGenerator(projectPath, projectName, ".gitignore", "springboot_gitignore.tml");
        Map<String, Object> smpom = new HashMap<>();
        smpom.put("project_name", projectName);
        mdGenerator.generator(smpom);
    }

    private void generateServicePom(String projectPath, String projectName, String groupId, String author, String versionId, boolean needTomcat, int dubboVersion) {
        String tmlName = "rcurve/pom_service.tml";
        //生成service module 下的pom文件
        PomGenerator servicePomGenerator = new PomGenerator(projectPath, projectName + File.separator + projectName + "-service", tmlName);
        Map<String, Object> smpom = new HashMap<>();
        smpom.put("groupId", groupId);
        smpom.put("parent_artifactId", projectName);
        smpom.put("artifactId", projectName + "-service");
        smpom.put("version", versionId + "-SNAPSHOT");
        smpom.put("version_id", versionId);
        smpom.put("author", author);
        smpom.put("api_artifactId", projectName + "-api");
        smpom.put("common_artifactId", projectName + "-common");
        servicePomGenerator.generator(smpom);
    }

    private void generateServerPom(String projectPath, String projectName, String packageName, String groupId, String author, String versionId, int dubboVersion) {
        String templateName = "rcurve/pom_server.tml";
        //生成service module 下的pom文件
        PomGenerator servicePomGenerator = new PomGenerator(projectPath, projectName + File.separator + projectName + "-server", templateName);
        Map<String, Object> smpom = new HashMap<>();
        smpom.put("groupId", groupId);
        smpom.put("package", packageName);
        smpom.put("parent_artifactId", projectName);
        smpom.put("artifactId", projectName + "-server");
        smpom.put("version", versionId + "-SNAPSHOT");
        smpom.put("bootstrap", this.adapterProjectNameToCamelName(projectName) + "Bootstrap");
        smpom.put("service_artifactId", projectName + "-service");
        servicePomGenerator.generator(smpom);
    }


    private String adapterProjectNameToCamelName(String name) {
        if (StringUtils.isEmpty(name)) {
            return "";
        }
        try {
            String[] strings = name.split("-");
            if (strings.length > 1) {
                String res = "";
                for (int i = 0; i < strings.length; i++) {
                    if (!StringUtils.isEmpty(strings[i])) {
                        res = res + StringUtils.capitalize(strings[i]);
                    }
                }
                return res;
            }
        } catch (Exception e) {
            return StringUtils.capitalize(name);
        }
        return StringUtils.capitalize(name);
    }

}
