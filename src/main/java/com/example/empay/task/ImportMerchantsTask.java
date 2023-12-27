package com.example.empay.task;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class ImportMerchantsTask implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        Arrays.asList(args).stream().forEach(a -> System.out.println(a));
        if (!(args.length > 0 && args[0].equals("-task import:merchants"))) return;
        System.out.println("Importing merchants ...");
        System.exit(0);
    }
}
