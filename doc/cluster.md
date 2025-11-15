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
    --from-literal=GDEVXY_DATABASE_USERNAME=<secret> \
    --from-literal=GDEVXY_DATABASE_PASSWORD=<secret> \
    --from-literal=GRAVATAR_API_KEY=<secret> \
    --from-literal=CONTENTFUL_CDA_TOKEN=<secret> \
    --from-literal=CONTENTFUL_CMA_TOKEN=<secret> \
    --from-literal=CONTENTFUL_CPA_TOKEN=<secret> \
    --from-literal=HMAC_SECRET=<secret>
```

for editing secrets:

```script
kubectl edit secret blog-secrets
```

## Configure SSL

cert-manager

```script
kubectl create namespace cert-manager
kubectl apply -f https://github.com/jetstack/cert-manager/releases/download/v<VERSION>/cert-manager.crds.yaml
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v<VERSION>/cert-manager.yaml
```

## Deploy

```script
kubectl apply -f ./.deploy/deployment.yaml
```

```script
kubectl apply -f ./.deploy/cluster-ip.yaml
```

internal ingress

```script
kubectl apply -f ./.deploy/ingress.yaml
```

issue domain certificates

```script
kubectl apply -f ./.deploy/issuer.yaml
```

reverse (public) proxy
make sure that the [{VERSION}](https://github.com/kubernetes/ingress-nginx) is aligned with k8s's version, e.g v1.12.0-beta.0 goes with kube 1.31.x

```script
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v{VERSION}/deploy/static/provider/cloud/deploy.yaml
```

## Verify SSL

```script
kubectl get certificate gdevxy-tls -o yaml
```

if the certificate was not emitted it could be related to the DNS TXT record challenge to update. Check what is wrong and act accordingly.

some debugging actions

```script
kubectl describe clusterissuer letsencrypt-prod
kubectl describe ingress gdevxy-ingress
```

## Database

Add egress rule to load balancer config for port <XXXX>
