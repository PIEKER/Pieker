package pieker.app;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import pieker.api.assertions.Assert;

import java.io.File;
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
public class PluginManager {

    public PluginManager(String pluginDir){
        loadPluginsFromDir(Path.of(pluginDir));
    }

    private final Map<String, Assert> plugins = new HashMap<>();

    public void loadPluginsFromDir(Path pluginDir) {
        File[] jars = pluginDir.toFile().listFiles((_, name) -> name.endsWith(".jar"));
        if (jars == null) return;

        URL[] urls = Arrays.stream(jars).map(file -> {
            try {
                return file.toURI().toURL();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e); //FIXME
            }
        }).toArray(URL[]::new);

        URLClassLoader loader = new URLClassLoader(urls, this.getClass().getClassLoader());
        ServiceLoader<Assert> serviceLoader = ServiceLoader.load(Assert.class, loader);

        for (Assert plugin : serviceLoader) {
            plugins.put(plugin.getIdentifier(), plugin);
        }
    }
}
