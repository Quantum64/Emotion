package co.q64.emotion.lang;

import co.q64.emotion.lang.value.Value;
import lombok.Getter;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Getter
public class LocalRegisters {
    private Map<Value, Value> data = new HashMap<Value, Value>();

    protected @Inject LocalRegisters() {}
}
