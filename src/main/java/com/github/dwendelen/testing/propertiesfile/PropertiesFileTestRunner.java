package com.github.dwendelen.testing.propertiesfile;

import org.junit.runner.Runner;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PropertiesFileTestRunner extends Suite {
    private final List<Runner> runners;

    public PropertiesFileTestRunner(final Class<?> klass) throws InitializationError {
        super(klass, new ArrayList<Runner>());
        final PropertiesFileTest annotation = klass.getAnnotation(PropertiesFileTest.class);
        if (annotation == null) {
            throw new InitializationError("No @PropertyFileTest");
        }

        List<ResourcePropertySource> sourcesToMerge = getMergeSources(annotation.mergeWith());

        runners = new ArrayList<>();
        for (final Resource resource : getPropertySources(annotation.filePattern(), annotation.excludeFilesStartingWith())) {
            final List<ResourcePropertySource> propertySources = mapToSource(resource);
            propertySources.addAll(sourcesToMerge);
            runners.add(new PropertySourceRunner(klass, propertySources, resource.getFilename()));
            runners.add(new ExpectedPropertiesRunner(klass, propertySources, resource.getFilename(), annotation.expectedProperties()));
        }
    }

    @Override
    protected List<Runner> getChildren() {
        return runners;
    }

    private List<Resource> getPropertySources(String pattern, String exclude) {
        try {
            PathMatchingResourcePatternResolver resourceProvider = new PathMatchingResourcePatternResolver();
            Resource[] resources = resourceProvider.getResources(pattern);
            return filterExcludedResources(resources, exclude);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<Resource> filterExcludedResources(Resource[] resources, String exclude) {
        List<Resource> filteredList = new ArrayList<>();
        for (Resource resource : resources) {
            if(fileNameIsExcluded(resource.getFilename(), exclude)) {
                continue;
            }

            filteredList.add(resource);
        }
        return filteredList;
    }

    private boolean fileNameIsExcluded(String filename, String exclude) {
        if (PropertiesFileTest.EXCLUDE_FILES_STARTING_WITH_DEFAULT_VALUE.equals(exclude)) {
            return false;
        }

        return filename.startsWith(exclude);
    }

    private List<ResourcePropertySource> mapToSource(Resource resource) {
        try {
            ResourcePropertySource resourcePropertySource = new ResourcePropertySource(resource);
            List<ResourcePropertySource> propertySources = new ArrayList<>();
            propertySources.add(resourcePropertySource);
            return propertySources;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<ResourcePropertySource> getMergeSources(String stringPath) {
        if(StringUtils.isEmpty(stringPath)) {
            return new ArrayList<>();
        }

        DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource(stringPath);
        return mapToSource(resource);
    }
}
