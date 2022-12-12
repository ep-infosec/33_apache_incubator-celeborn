# How to Deploy RSS on Kubernetes

## Prerequisite
Celeborn is recommended to be deployed on nodes with local disk. Before starting, please make sure local disks on nodes are mounted to specific path.

## [Optional] Build Celeborn docker image
We have provided a docker image for Celeborn in helm chart. If you want to build your own Celeborn image, run docker build with our Dockerfile.

You should download or build your binary package first, then decompress it, cd decompress directory, use following command to build docker image.

`
docker build -f docker/Dockerfile -t ${your-repo}:${tag} .
`

## Deploy Celeborn with helm

#### Install kubectl and Helm

Please install and config kubectl and Helm first. See [Installing kubectl](https://kubernetes.io/docs/tasks/tools/#kubectl) and [Installing Helm](https://helm.sh/docs/intro/install/).

#### Create namespace on kubernetes
`
kubectl create ns rss
`

#### [Optional] Modify helm values file values.yaml
You can modify helm values file and set up customed deployment configuration.
`
vim ${CELEBORN_HOME}/docker/helm/values.yaml
`
These values are suggested to be checked before deploy:  
- masterReplicas (number of Celeborn Master)
- workerReplicas (number of Celeborn Worker)
- celebornVersion (Celeborn release version)
- celeborn.worker.storage.dirs (local disk mount path on k8s node)

For more information of Celeborn configurations, see [CONFIGURATIONS](../CONFIGURATION_GUIDE.md)

#### Install Celeborn
`
helm install celeborn-helm ${CELEBORN_HOME}/docker/helm -n ${celeborn namespace}
`

#### Connect to Celeborn in K8s pod
After installation, you can connect to Celeborn master through headless service. For example, this is the spark configuration for 3-master RSS:
`
spark.celeborn.master.endpoints=shuffleservice-master-0.rss-master-svc.${celeborn namespace},shuffleservice-master-1.rss-master-svc.${celeborn namespace},shuffleservice-master-2.rss-master-svc.${celeborn namespace}
`

#### Uninstall Celeborn
`
helm uninstall celeborn-helm -n ${celeborn namespace}
`