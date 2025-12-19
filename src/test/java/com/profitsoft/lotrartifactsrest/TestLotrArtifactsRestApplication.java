package com.profitsoft.lotrartifactsrest;

import org.springframework.boot.SpringApplication;

public class TestLotrArtifactsRestApplication {

    public static void main(String[] args) {
        SpringApplication.from(LotrArtifactsRestApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
