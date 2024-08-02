#!/usr/bin/env bash

set -e

PROD_IMAGE_NAME="${REGISTRY}/${IMAGE_NAME}-prod"
NONPROD_IMAGE_NAME="${REGISTRY}/${IMAGE_NAME}-nonprod"
DOCKER_TAG=$1

docker_tag_and_push() {
  local source_image_name=$1
  local target_image_name=$2
  local tag=$3

  echo "Pushing ${target_image_name}:${tag}..."
  docker tag "${source_image_name}" "${target_image_name}:${tag}"
  docker push "${target_image_name}:${tag}"
  echo "Done."
}

main() {
	case "$TRAVIS_BRANCH" in
		development)
			docker_tag_and_push "${IMAGE_NAME}" "${NONPROD_IMAGE_NAME}" "${DOCKER_TAG}"
			;;

		*)
			docker_tag_and_push "${IMAGE_NAME}" "${PROD_IMAGE_NAME}" "${DOCKER_TAG}"
			;;
	esac
}

main
