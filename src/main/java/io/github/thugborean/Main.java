package io.github.thugborean;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import io.github.thugborean.repl.REPL;
import io.github.thugborean.vm.VM;

public class Main {
    public static void main(String[] args ) {

        if(args.length > 0) {
            try {
                String source = Files.readString(Path.of(args[0]));
                VM vm = new VM();
                vm.execute(source);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            REPL repl = new REPL();
            repl.run();
        }
    }
}