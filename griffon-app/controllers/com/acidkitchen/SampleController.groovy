package com.acidkitchen

import com.jcraft.jsch.Channel
import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import griffon.core.artifact.GriffonController
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import griffon.transform.Threading
import javafx.application.Platform

import javax.annotation.Nonnull

@ArtifactProviderFor(GriffonController)
class SampleController {
    @MVCMember @Nonnull
    SampleModel model
    private ChannelExec exec;

    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void restart(){
        model.processStatus = "RESTART IN PROGRESS..."
        runCommand("ece restart", {
            if(it == 0){
                model.processStatus = model.customIpAddress + " has restarted"
            } else {
                model.processStatus = "An error occured during "+model.customIpAddress+" restart"
            }
        })
    }

    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void checkStatus(){
        model.processStatus = "RETRIEVING ECE STATUS..."
        runCommand("ece status", {
            if(it == 0){
                model.processStatus = "STATUS RETRIEVED"
            } else {
                model.processStatus = "An error occured"
            }
        })
    }

    @Threading(Threading.Policy.OUTSIDE_UITHREAD)
    void runCommand(String command, Closure after){

        Runnable runnable = new Runnable() {
            @Override
            void run() {
                run(command, after);
            }
        }

        Thread thread = new Thread(runnable);
        thread.start();
    }

    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void runEceStudio(){
        String domain = "dev140.jp-dev.jppol.net:8080"
        if(model.customIpAddress.contains("10.10.97") || model.customIpAddress.contains("jp-dev-kiev")){ //kiev devs
            domain = "dev236.jp-dev-kiev.jppol.net:8080"
        } else if (model.customIpAddress.contains("172.24.27") || model.customIpAddress.contains("jp-dev")){ //dk devs
            domain = "dev140.jp-dev.jppol.net:8080"
        } else if (model.customIpAddress.contains("172.24.25") || model.customIpAddress.contains("staging")){ //staging
            domain = "theia145.jp-staging.jppol.net:8080"
        } else if (model.customIpAddress.contains("172.24.24") || model.customIpAddress.contains("prod")){
            domain = "studio.jp-prod.jppol.net"
        }
        "javaws http://${domain}/studio/Studio.jnlp?user.country=&user.language=&com.escenic.sso.enabled=false&com.escenic.sso.provider=Google&launcher=Studio.jnlp".execute()
    }

    void run(String command, Closure after){
        Properties config = new Properties()
        config.put("StrictHostKeyChecking", "no")

        JSch jSch = new JSch()
        Session session = jSch.getSession("escenic", model.customIpAddress, 22);
        session.with {
            setConfig(config)
            setPassword("bogus")
            connect(10*1000)
            Channel channel = openChannel("exec")
            exec = (ChannelExec) channel

            exec.setCommand(command)
            InputStream inputStream = exec.getInputStream()
            exec.connect()

            byte[] tmp = new byte[1024];
            String readText = "";
            while (true) {
                while (inputStream.available() > 0) {
                    int i = inputStream.read(tmp, 0, 1024);
                    if (i < 0)
                        break;
                    readText = new String(tmp, 0, i);

                    System.out.print(readText);

                    runLater {
                        model.status = readText.contains("UP") ? "ONLINE" : "OFFLINE"
                    }
                }
                if (channel.isClosed()) {
                    System.out.println("exit-status: "
                            + channel.getExitStatus());

                    runLater {after(channel.getExitStatus())}
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }
        }
    }

    void runLater(Closure closure){
        Platform.runLater(new Runnable() {
            @Override
            void run() {
                closure()
            }
        })
    }
}