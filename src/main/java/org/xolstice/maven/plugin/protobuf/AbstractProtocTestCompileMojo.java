package org.xolstice.maven.plugin.protobuf;

/*
 * Copyright (c) 2016 Maven Protocol Buffers Plugin Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.common.collect.ImmutableList;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.List;

/**
 * An abstract base mojo configuration for using {@code protoc} compiler with the test sources.
 *
 * @since 0.3.3
 */
public abstract class AbstractProtocTestCompileMojo extends AbstractProtocMojo {

    /**
     * The source directories containing the test {@code .proto} definitions to be compiled.
     */
    @Parameter(
            required = true,
            defaultValue = "${basedir}/src/test/proto"
    )
    private File protoTestSourceRoot;

    /**
     * The additional source directories containing the test {@code .proto} definitions to be compiled.
     */
    @Parameter(
            required = false
    )
    private File[] additionalProtoTestSourceRoots;

    /**
     * This is the directory into which the (optional) descriptor set file will be created.
     *
     * @since 0.3.0
     */
    @Parameter(
            required = true,
            defaultValue = "${project.build.directory}/generated-test-resources/protobuf/descriptor-sets"
    )
    private File descriptorSetOutputDirectory;

    /**
     * If generated descriptor set is to be attached to the build, specifies an optional classifier.
     *
     * @since 0.4.1
     */
    @Parameter(
            required = false,
            defaultValue = "test"
    )
    protected String descriptorSetClassifier;

    @Override
    protected void doAttachProtoSources() {
        projectHelper.addTestResource(project, getProtoSourceRoot().getAbsolutePath(),
                ImmutableList.copyOf(getIncludes()), ImmutableList.copyOf(getExcludes()));

        final File[] additionalSources = getAdditionalProtoSourceRoots();

        if (null != additionalSources) {
            for (File source : additionalSources) {
                projectHelper.addResource(project, source.getAbsolutePath(),
                        ImmutableList.copyOf(getIncludes()), ImmutableList.copyOf(getExcludes()));
            }
        }
    }

    @Override
    protected void doAttachGeneratedFiles() {
        final File outputDirectory = getOutputDirectory();
        project.addTestCompileSourceRoot(outputDirectory.getAbsolutePath());
        if (writeDescriptorSet) {
            final File descriptorSetFile = new File(getDescriptorSetOutputDirectory(), descriptorSetFileName);
            projectHelper.attachArtifact(project, "test-protobin", descriptorSetClassifier, descriptorSetFile);
        }
        buildContext.refresh(outputDirectory);
    }

    @Override
    protected List<Artifact> getDependencyArtifacts() {
        return project.getTestArtifacts();
    }

    @Override
    protected File getDescriptorSetOutputDirectory() {
        return descriptorSetOutputDirectory;
    }

    @Override
    protected File getProtoSourceRoot() {
        return protoTestSourceRoot;
    }

    @Override
    protected File[] getAdditionalProtoSourceRoots() {
        return additionalProtoTestSourceRoots;
    }
}

