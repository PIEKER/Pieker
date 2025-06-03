package pieker.common.plugin;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pieker.api.assertions.Assert;
import pieker.api.assertions.StubAssert;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

@Slf4j
@Getter
@NoArgsConstructor
public class PluginManager {

    private boolean ignoreUnknownPluginReferences = false;

    public PluginManager(String pluginDir){
        loadPluginsFromDir(Path.of(pluginDir));
        this.ignoreUnknownPluginReferences = Boolean.parseBoolean(System.getProperty("ignoreMissingPluginReferences", "false"));

        if (this.ignoreUnknownPluginReferences) log.info("ignoring unknown plugin-references.");
    }

    private final Map<String, Class<? extends Assert>> pluginRegistry = new HashMap<>();

    public void loadPluginsFromDir(Path pluginDir) {
        File[] jars = pluginDir.toFile().listFiles((_, name) -> name.endsWith(".jar"));
        if (jars == null) return;

        URL[] urls = Arrays.stream(jars).map(file -> {
            try {
                return file.toURI().toURL();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }).toArray(URL[]::new);

        URLClassLoader loader = new URLClassLoader(urls, this.getClass().getClassLoader());
        ServiceLoader<Assert> serviceLoader = ServiceLoader.load(Assert.class, loader);

        for (Assert pluginInstance : serviceLoader) {
            Class<? extends Assert> pluginClass = pluginInstance.getClass();
            pluginRegistry.put(pluginInstance.getAssertPlugin(), pluginClass);
        }
    }

    public Assert createPluginInstance(String assertPlugin, String arguments) throws ClassNotFoundException {
        Class<? extends Assert> clazz = pluginRegistry.get(assertPlugin);
        if (this.ignoreUnknownPluginReferences && clazz == null) {
            return new StubAssert();
        } else if (clazz == null) {
            throw new ClassNotFoundException("unable to find plugin implementation for plugin-reference: " + assertPlugin);
        }
        try {
            Constructor<? extends Assert> constructor = clazz.getConstructor(String.class);
            return constructor.newInstance(arguments);
        } catch (Exception e) {
            throw new ClassNotFoundException("Failed to instantiate plugin: " + assertPlugin, e);
        }
    }

    @Override
    public String toString() {
        if (this.pluginRegistry.isEmpty()) return "None";

        StringBuilder s = new StringBuilder();
        this.pluginRegistry.forEach((k,v) -> s.append(k).append("::").append(v).append(" || "));
        return s.substring(0, s.length()-4);
    }
}
