# Weather-Stations-Monitoring

# Project Description
## What it does?
The Weather Stations Monitoring System is an innovative application that leverages the power of microservice architecture and data-intensive processing to collect, analyze, and monitor real-time weather data from distributed weather stations. The system aims to provide accurate and timely weather information for various purposes such as weather forecasting, agricultural planning, and disaster management.

The application is designed to handle the massive influx of data streams generated by a multitude of weather stations. These weather stations continuously emit readings for the current weather status, including **humidity**, **temperature**, and **wind speed**. The system efficiently processes and persists this high-frequency data for further analysis and archiving.

## Why you used the technologies you used?
To tackle the challenges posed by the large-scale data streams, we adopted a **microservice architecture**. The system is divided into three stages: **data acquisition**, **data processing**, and **archiving, and indexing**. Multiple weather stations feed their readings into a queueing service (``Kafka``) in the data acquisition stage. The **base central station** consumes the streamed data and archives it in the form of ``Parquet files`` in the data processing and archiving stage. Additionally, two variants of indexing are maintained: a key-value store (``Bitcask``) for the latest reading from each station and ``ElasticSearch/Kibana`` running over the Parquet files.

The choice of technologies was driven by their suitability for handling large volumes of data and enabling efficient data processing and analysis. Kafka was chosen as the queueing service due to its ability to handle high-throughput, real-time data streams. Parquet files were selected for archiving weather statuses, as they provide a columnar storage format optimized for analytics workloads. ElasticSearch and Kibana were used for indexing and querying the data, enabling rich and interactive visualizations.

## Some of the challenges 
During the development of the **Weather Stations Monitoring System**, we encountered several challenges. Managing the high-frequency data streams and ensuring reliable data acquisition were among the primary challenges. We implemented the ``Kafka Processors`` to detect **rainy conditions** based on humidity levels, enhancing the system's functionality. Another challenge was efficiently **storing and managing the updated view of weather statuses**. We implemented ``BitCask Riak`` as a key-value store, leveraging its benefits such as **hint files** and **scheduled compaction**.

## Additional features
Integrating the system with external data sources, such as the ``Open-Meteo`` weather API, to augment the data collected from weather stations. This will further enrich the accuracy and reliability of the weather information provided by the system. Additionally, we incorporated more **Enterprise Integration Patterns** to enhance the system's capabilities and make it more flexible and scalable.

# How to Install and Run the Project
To install and run the project, follow the steps below:

1. Create the persistent volume claim by applying the `base-volume-claim.yaml` file:
```
kubectl apply -f base-volume-claim.yaml
```

2. Create the persistent volume by applying the `base-volume.yaml` file:
```
kubectl apply -f base-volume.yaml
```

3. Deploy the central station by applying the `centeral-station.yaml` file:
```
kubectl apply -f centeral-station.yaml
```

4. Build the Docker image for the central station:
```
docker build -t base-station:1.0 .
```

5. Deploy ElasticSearch and Kibana by applying the `elk.yaml` file:
```
kubectl apply -f elk.yaml
```

6. Create a ConfigMap for Kafka configuration by applying the `kafka-config.yaml` file:
```
kubectl apply -f kafka-config.yaml
```

7. Deploy Kafka by applying the `kafka.yaml` file:
```
kubectl apply -f kafka.yaml
```

8. Create an Ingress for Kibana by applying the `kibana-ingress.yaml` file:
```
kubectl apply -f kibana-ingress.yaml
```

9. Deploy ZooKeeper by applying the `zookeeper.yaml` file:
```
kubectl apply -f zookeeper.yaml
```

10. Deploy the remote station by applying the `remote-station.yaml` file:
```
kubectl apply -f remote-station.yaml
```

11. Build the Docker image for the remote station:
```
docker build -t remote-station:1.0 .
```

12. Build the Docker image for the KStream:
```
docker build -t kstream:1.0 .
```

13. Deploy the stream by applying the `stream.yaml` file:
```
kubectl apply -f stream.yaml
```

14. Deploy the weather stations by applying the `weather-station.yaml` file:
```
kubectl apply -f weather-station.yaml
```

15. Build the Docker image for the weather stations:
```
docker build -t weather-station:1.0 .
```

Once all the deployments are successfully applied, the project should be up and running. You can use other Kubernetes commands to check the status and logs of the deployed services.

# How to use the Project
To use the Weather Stations Monitoring System project, follow the steps below:

1. Ensure that you have successfully installed and run the project as described in the previous instructions.

2. Access the central station by using its service endpoint. Depending on your Kubernetes setup, you can use the following command to retrieve the external IP address:
```
kubectl get services
```
Look for the service named "base-station-deployment" and note down the external IP address.

3. Open a web browser and enter the external IP address of the central station along with the appropriate port (e.g., http://<external-ip>:9092). This will allow you to access the central station's web interface.

4. On the central station's web interface, you can view and analyze the real-time weather data received from the distributed weather stations. The interface may provide various features such as visualizations, charts, and statistics related to weather conditions.

5. Use the navigation or search functionality within the interface to explore different aspects of the weather data. You may be able to filter data by specific weather stations, time intervals, or other relevant parameters.

6. Additionally, you can access Kibana for more advanced data analysis and visualization. Open a web browser and enter the external IP address of the Kibana service, followed by the appropriate port (e.g., http://<kibana-external-ip>:5601). Kibana provides a user-friendly interface to explore and interact with the indexed weather data.

7. In Kibana, you can create customized visualizations, dashboards, and perform complex queries to gain deeper insights into the weather data. Utilize the provided features and tools to analyze historical trends, identify patterns, and generate meaningful reports.

By following these steps, you can effectively use the Weather Stations Monitoring System project to monitor, analyze, and gain insights from real-time weather data collected from distributed weather stations.

# Team members
[Mohamed magdy](https://github.com/muhmagdy)
[Youssef saber](https://github.com/youssefsaber0)
[Ahmed ElSaeed](https://github.com/Ahmedelsa3eed)
[Ziad reda](https://github.com/ziadreda72)