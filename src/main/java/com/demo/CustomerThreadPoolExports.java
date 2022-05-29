package com.demo;

import io.prometheus.client.Collector;
import io.prometheus.client.GaugeMetricFamily;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;


public class CustomerThreadPoolExports extends Collector {

    private static final String THREAD_POOL_CORE_SIZE = "thread_pool_core_size";
    private static final String THREAD_POOL_LARGEST_SIZE = "thread_pool_largest_size";
    private static final String THREAD_POOL_MAX_SIZE = "thread_pool_max_size";
    private static final String THREAD_POOL_ACTIVE_SIZE = "thread_pool_active_size";
    private static final String THREAD_POOL_THREAD_COUNT = "thread_pool_thread_count";
    private static final String THREAD_POOL_QUEUE_SIZE = "thread_pool_queue_size";

    private final ThreadPoolExecutor poolExecutor;

    private final List<String> labelNames = Arrays.asList("threadPoolName");
    private final List<String> labelValues;

    public CustomerThreadPoolExports(ThreadPoolExecutor poolExecutor , String threadPoolName) {
        this.poolExecutor = poolExecutor;
        this.labelValues = Arrays.asList(threadPoolName);
    }

    void addThreadMetrics(List<MetricFamilySamples> sampleFamilies) {

        GaugeMetricFamily gaugeMetricFamily = new GaugeMetricFamily(
                THREAD_POOL_CORE_SIZE,
                THREAD_POOL_CORE_SIZE,
                labelNames);
            gaugeMetricFamily.addMetric(labelValues, poolExecutor.getCorePoolSize());

        sampleFamilies.add(gaugeMetricFamily);


        gaugeMetricFamily = new GaugeMetricFamily(
                THREAD_POOL_LARGEST_SIZE,
                THREAD_POOL_LARGEST_SIZE,
                labelNames);
        gaugeMetricFamily.addMetric(labelValues, poolExecutor.getLargestPoolSize());

        sampleFamilies.add(gaugeMetricFamily);

        gaugeMetricFamily = new GaugeMetricFamily(
                THREAD_POOL_MAX_SIZE,
                THREAD_POOL_MAX_SIZE,
                labelNames);

        gaugeMetricFamily.addMetric(labelValues, poolExecutor.getMaximumPoolSize());

        sampleFamilies.add(gaugeMetricFamily);

        gaugeMetricFamily = new GaugeMetricFamily(
                THREAD_POOL_ACTIVE_SIZE,
                THREAD_POOL_ACTIVE_SIZE,
                labelNames);

        gaugeMetricFamily.addMetric(labelValues, poolExecutor.getActiveCount());

        sampleFamilies.add(gaugeMetricFamily);

        gaugeMetricFamily =  new GaugeMetricFamily(
                THREAD_POOL_THREAD_COUNT,
                THREAD_POOL_THREAD_COUNT,
                labelNames);

        gaugeMetricFamily.addMetric(labelValues, poolExecutor.getPoolSize());

        sampleFamilies.add(gaugeMetricFamily);

        gaugeMetricFamily = new GaugeMetricFamily(
                THREAD_POOL_QUEUE_SIZE,
                THREAD_POOL_QUEUE_SIZE,
                labelNames);

        gaugeMetricFamily.addMetric(labelValues, poolExecutor.getQueue().size());

        sampleFamilies.add(gaugeMetricFamily);

    }

    @Override
    public List<MetricFamilySamples> collect() {
        List<MetricFamilySamples> mfs = new ArrayList<>();
        addThreadMetrics(mfs);
        return mfs;
    }

}
