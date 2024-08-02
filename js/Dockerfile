FROM docker.io/{ORGANIZATION}/nodejs:16 AS base

ARG ARTIFACTORY_API_KEY=${ARTIFACTORY_API_KEY:-ARTIFACTORY_API_KEY_NOT_SET}

WORKDIR /usr/src/app

COPY ["package.json", "pnpm-lock.yaml", "./"]

RUN touch .npmrc && \
    echo "//registry.npmjs.org/:_authToken=" >> .npmrc && \
    curl -H "X-JFrog-Art-Api:${ARTIFACTORY_API_KEY}" https://na.artifactory.swg-devops.com/artifactory/api/npm/wcp-tmp-ace-fr-team-npm-virtual/auth/console >> .npmrc && \
    echo "strict-peer-dependencies=false" >> .npmrc

RUN pnpm add @contrast/agent@4.23.1 && pnpm install --no-optional --frozen-lockfile && rm .npmrc

FROM docker.io/{ORGANIZATION}/nodejs:16

ENV NODE_ENV=production

WORKDIR /usr/src/app
RUN chmod 775 /usr/src/app

COPY --from=base /usr/src/app/ .
COPY . .

RUN pnpm build

RUN chmod 775 /usr/src/app/dist/assets/*.js

EXPOSE 3000

CMD ["pnpm", "start"]
