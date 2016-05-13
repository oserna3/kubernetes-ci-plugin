package com.elasticbox.jenkins.k8s.plugin.builders;

import com.elasticbox.jenkins.k8s.auth.Authentication;
import com.elasticbox.jenkins.k8s.chart.ChartRepo;
import com.elasticbox.jenkins.k8s.plugin.clouds.ChartRepositoryConfig;
import com.elasticbox.jenkins.k8s.plugin.clouds.KubernetesCloud;
import com.elasticbox.jenkins.k8s.plugin.util.PluginHelper;
import com.elasticbox.jenkins.k8s.plugin.util.TaskLogger;
import com.elasticbox.jenkins.k8s.services.error.ServiceException;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;

public class DeployChartBuildStep extends BaseChartBuildStep {
    private static final Logger LOGGER = Logger.getLogger(DeployChartBuildStep.class.getName() );

    private static final String NAME_PREFIX = "DeployChartBS-";

    @DataBoundConstructor
    public DeployChartBuildStep(String id, String cloudName, String chartsRepo, String chartName) {
        super();
        this.id = StringUtils.isNotEmpty(id)  ? id : NAME_PREFIX + UUID.randomUUID().toString();
        this.cloudName = cloudName;
        this.chartsRepo = chartsRepo;
        this.chartName = chartName;
        injectMembers();
    }

    @Override
    protected void doPerform(TaskLogger taskLogger, KubernetesCloud kubeCloud, ChartRepo chartRepo)
            throws ServiceException {

        taskLogger.info("Deploying chart: " + getChartName());
        deploymentService.deployChart(getCloudName(), kubeCloud.getNamespace(), chartRepo, getChartName() );
        taskLogger.info("Chart [" + getChartName() + "] deployed");
    }

    @Extension
    public static final class DescriptorImpl extends BaseChartBuildStep.DescriptorImpl {

        private static final String KUBERNETES_DEPLOY_CHART = "Kubernetes - Deploy Chart";

        @Override
        public String getDisplayName() {
            return KUBERNETES_DEPLOY_CHART;
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }
    }
}