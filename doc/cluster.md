# Cluster Configuration

## Pre-requisites

- accessible container registry 
  - create policy `Allow dynamic-group <group> to read repos in compartment <cptm>`
- configured [OKE cluster](https://docs.oracle.com/en-us/iaas/Content/ContEng/Tasks/contengcreatingclusterusingoke_topic-Using_the_Console_to_create_a_Quick_Cluster_with_Default_Settings.htm)
- oci-cli installed (and configured, i.e: `oci setup`)
- kubectl installed

## Configure secrets

Authorize k8s to access docker-registry

```script
kubectl create secret docker-registry docker-password --docker-server={domain} --docker-username={tenantid}/{email} --docker-password='{auth-api}' --docker-email={email}
```

```script
kubectl create secret generic blog-secrets \
    --from-literal=GRAVATAR_API_KEY=<secret> \
    --from-literal=CONTENTFUL_CDA_TOKEN=<secret> \
    --from-literal=CONTENTFUL_CPA_TOKEN=<secret>
```

## Configure SSL

cert-manager

```script
kubectl create namespace cert-manager
kubectl apply -f https://github.com/jetstack/cert-manager/releases/download/v<VERSION>/cert-manager.crds.yaml
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v<VERSION>/cert-manager.yaml
```

reverse (public) proxy
make sure that the [{VERSION}](https://raw.githubusercontent.com/kubernetes/ingress-nginx) is aligned with k8s's version

```script
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-{VERSION}/deploy/static/provider/cloud/deploy.yaml
```

internal ingress

```script
kubectl apply -f ./.deploy/ingress.yaml
```

issue domain certificates

```script
kubectl apply -f ./.deploy/issuer.yaml
```

## Configure Cluster-IP

```script
kubectl apply -f ./.deploy/cluster-ip.yaml
```

## Deploy application

```script
kubectl apply -f ./.deploy/deployment.yaml
```
