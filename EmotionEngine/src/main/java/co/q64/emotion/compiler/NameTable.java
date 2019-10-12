package co.q64.emotion.compiler;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class NameTable {
    protected @Inject Provider<NameTableResult> nameTableResultProvider;
    protected @Inject CompilerOutputFactory compilerOutputFactory;

    private int count;
    private Map<String, Integer> names = new HashMap<>();

    protected @Inject NameTable() {}

    public int createName(String name) {
        return names.computeIfAbsent(name, n -> count++);
    }

    public boolean hasName(String name) {
        return names.containsKey(name);
    }

    public Optional<Integer> getName(String name) {
        return Optional.ofNullable(names.get(name));
    }

    public NameTableResult getSaveInstructions(String name) {
        return getInstructions(name, true);
    }

    public NameTableResult getLoadInstructions(String name) {
        return getInstructions(name, false);
    }

    private NameTableResult getInstructions(String name, boolean save) {
        NameTableResult result = nameTableResultProvider.get();
        switch (name) {
            case "a":
            case "b":
            case "c":
            case "d":
                result.setResult(Optional.of(Arrays.asList((save ? "sdr " : "ldr ") + name)));
                break;
            default:
                if (!save && !hasName(name)) {
                    result.setError(Optional.of(compilerOutputFactory.create("Name not found: '" + name + "'")));
                    break;
                }
                result.setResult(Optional.of(Arrays.asList("load " + createName(name), save ? "sdv" : "ldv")));
                break;
        }
        return result;
    }

    @Getter
    @Setter(AccessLevel.PRIVATE)
    public static class NameTableResult {
        private Optional<CompilerOutput> error = Optional.empty();
        private Optional<List<String>> result = Optional.empty();

        protected @Inject NameTableResult() {}
    }
}
