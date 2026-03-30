package com.fastservice.platform.backend;

import java.nio.file.Path;

import com.fastservice.platform.backend.bootstrap.BackendBootstrap;

public final class BackendApplication {

    private BackendApplication() {
    }

    public static void main(String[] args) {
        boolean loadDemoData = Boolean.parseBoolean(System.getProperty("fsp.demo-data", "false"));
        Path baseDir = Path.of(System.getProperty("fsp.base-dir", "target/lealone-data"));
        BackendBootstrap.start(BackendBootstrap.DEFAULT_DATABASE, baseDir, loadDemoData);
    }
}
