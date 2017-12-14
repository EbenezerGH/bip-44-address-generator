package jfyp.addressgenerator.addressgenerator;

import java.security.SecureRandom;

import io.github.novacrypto.bip39.MnemonicGenerator;
import io.github.novacrypto.bip39.Words;
import io.github.novacrypto.bip39.wordlists.English;

public class WordsGenerator {
    private static WordsGenerator instance;

    private WordsGenerator() {
    }

    public static WordsGenerator GetInstance() {
        if (instance == null) {
            instance = new WordsGenerator();
        }
        return instance;
    }

    public String generateNewMnemonic(Words wordCount) {
        StringBuilder sb = new StringBuilder();
        byte[] entropy = new byte[wordCount.byteLength()];
        new SecureRandom().nextBytes(entropy);
        new MnemonicGenerator(English.INSTANCE)
                .createMnemonic(entropy, sb::append);
        return sb.toString();
    }
}
