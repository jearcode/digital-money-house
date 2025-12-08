package com.dmh.accountservice.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class AccountNumberGenerator {

    private static final String[] WORDS = {
            "BeanRx", "JavRex", "SrvCore", "ObjFlow", "StrmFx", "HeapFx",
            "ThrdX", "ByteOps", "JaxRun", "BeanFx", "RsrcIO", "MiniGC",
            "JetFlow", "SyncUp", "FluxIO", "NPEKing", "Log4U", "GCByte",
            "StackR", "ProcFx", "SrvX", "ByteX", "CoreIO"
    };

    private static final int CVU_LENGTH = 22;
    private static final int ALIAS_WORDS = 3;

    public String generateCvu() {
        Random random = new Random();
        StringBuilder cvu = new StringBuilder();

        for (int i = 0; i < CVU_LENGTH; i++) {
            cvu.append(random.nextInt(10));
        }

        return cvu.toString();
    }

    public String generateAlias() {
        Random random = new Random();

        String word1 = getRandomWord(random);
        String word2 = getRandomWord(random);
        String word3 = getRandomWord(random);

        return word1 + "." + word2 + "." + word3;
    }

    private String getRandomWord(Random random) {
        return WORDS[random.nextInt(WORDS.length)];
    }

}