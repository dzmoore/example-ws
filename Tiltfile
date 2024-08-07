# -*- mode: Python -*-

# For more on Extensions, see: https://docs.tilt.dev/extensions.html
load('ext://restart_process', 'docker_build_with_restart')

gradlew = "./gradlew"
if os.name == "nt":
  gradlew = "gradlew.bat"

local_resource(
  'example-ws-compile',
  gradlew + ' bootJar && ' +
  'rm -rf build/jar-staging && ' +
  'java -Djarmode=layertools -jar $(find build/libs -name \'*.jar\') extract --destination build/jar-extracted && ' +
  'rsync --delete --inplace --checksum -r build/jar-extracted/ build/jar',
  deps=['src', 'build.gradle']
)

docker_build_with_restart(
  'example-ws-image',
  '.',
  dockerfile='Dockerfile',
  entrypoint=['java', 'org.springframework.boot.loader.launch.JarLauncher'],
  only=['build/libs', 'build/jar', 'Dockerfile'],
  live_update=[
    sync('./build/jar/dependencies', '/application'),
    sync('./build/jar/spring-boot-loader', '/application'),
    sync('./build/jar/snapshot-dependencies', '/application'),
    sync('./build/jar/application', '/application'),
  ],
)

k8s_yaml('kubernetes.yaml')
k8s_resource('example-ws', port_forwards=[port_forward(8000, name='example-ws')])


load('ext://helm_resource', 'helm_resource', 'helm_repo')

# https://github.com/bitnami/charts/tree/main/bitnami/keycloak
helm_resource(
  'keycloak', 
  'oci://registry-1.docker.io/bitnamicharts/keycloak',
  namespace='keycloak',
  flags=[
    '--set=postgresql.enabled=false',
    '--set=externalDatabase.host=keycloak-postgresql.keycloak.svc.cluster.local',
    '--set=externalDatabase.user=bn_keycloak',
    '--set=externalDatabase.password=password',
    '--set=externalDatabase.database=bitnami_keycloak',
    '--create-namespace'
  ],
  port_forwards=[port_forward(8001, container_port=8080, name='keycloak')]
)

# https://github.com/bitnami/charts/tree/main/bitnami/postgresql
helm_resource(
  'keycloak-postgresql', 
  'oci://registry-1.docker.io/bitnamicharts/postgresql',
  namespace='keycloak',
  flags=[
    '--set=global.postgresql.auth.username=bn_keycloak',
    '--set=global.postgresql.auth.password=password',
    '--set=global.postgresql.auth.database=bitnami_keycloak',
    '--create-namespace'
  ]
)

helm_repo(
  'crossplane-stable',
  'https://charts.crossplane.io/stable'
)

helm_resource(
  'crossplane',
  'crossplane-stable/crossplane',
  namespace='crossplane-system',
  resource_deps=['crossplane-stable'],
  flags=['--create-namespace']
)

k8s_yaml('provider-keycloak.yaml')

k8s_resource(
  objects=['provider-keycloak'],
  new_name='provider-keycloak',
  resource_deps=['crossplane']
)