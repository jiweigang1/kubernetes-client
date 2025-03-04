/**
 * Copyright (C) 2015 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.fabric8.kubernetes.client;

import io.fabric8.kubernetes.api.model.APIGroup;
import io.fabric8.kubernetes.api.model.APIGroupList;
import io.fabric8.kubernetes.api.model.APIResourceList;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.RootPaths;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;

import java.io.Closeable;
import java.net.URL;

public interface Client extends HttpClientAware, ConfigAware<Config>, Closeable {

  public enum Supports {

  }

  /**
   * Checks if the client can be adapted to an other client type and if that target client is supported.
   *
   * @param type The target client class.
   * @param <C> The target client type.
   * @return Returns true if a working {@link io.fabric8.kubernetes.client.extension.ExtensionAdapter} is found.
   * @deprecated if the client can test for support, then use adapt(type).isSupported() instead.
   */
  @Deprecated
  <C extends Client> Boolean isAdaptable(Class<C> type);

  /**
   * The logic will check for the existence of a handler or check the api server for support.
   *
   * @param type to check for support
   * @return boolean value indicating whether this type is supported
   */
  <R extends KubernetesResource> boolean supports(Class<R> type);

  /**
   * Checks for the api group. exact = false will scan all groups
   * for a suffix match. exact = true will look only for that apiGroup.
   *
   * @param apiGroup to check for
   * @param exact true for an exact match
   * @return true if there is a match
   */
  boolean hasApiGroup(String apiGroup, boolean exact);

  /**
   * Adapt the client to another type. This will not perform any check of whether the new client
   * type is supported. It may even return the same object if it already supports the given
   * client type.
   *
   * @param type the instance of {@link Client} to adapt.
   * @return The refined instance of the {@link Client}.
   */
  <C extends Client> C adapt(Class<C> type);

  URL getMasterUrl();

  String getApiVersion();

  String getNamespace();

  RootPaths rootPaths();

  /**
   * Returns true if this cluster supports the given API path or API Group ID
   *
   * @param path Path as string
   * @return returns boolean value indicating whether it supports.
   * @deprecated use {@link #supports(Class)} instead
   */
  @Deprecated
  boolean supportsApiPath(String path);

  @Override
  void close();

  /**
   * Returns the api groups
   *
   * @return the {@link APIGroupList} metadata
   */
  APIGroupList getApiGroups();

  /**
   * Return a single api group
   *
   * @param name of the group
   * @return the {@link APIGroup} metadata
   */
  APIGroup getApiGroup(String name);

  /**
   * Return the api resource metadata for the given groupVersion
   *
   * @param groupVersion the groupVersion - group/version
   * @return the {@link APIResourceList} for the groupVersion
   */
  APIResourceList getApiResources(String groupVersion);

  /**
   * Typed API for managing resources. Any properly annotated POJO can be utilized as a resource.
   * <br>
   * Note: this call is generally for use internally within the DSL, not by end users
   *
   * <p>
   * Note: your resource POJO (T in this context) must implement
   * {@link io.fabric8.kubernetes.api.model.Namespaced} if it is a namespace-scoped resource.
   * </p>
   *
   * @param resourceType Class for resource
   * @param <T> represents resource type. If it's a namespaced resource, it must implement
   *        {@link io.fabric8.kubernetes.api.model.Namespaced}
   * @param <L> represents resource list type
   * @param <R> represents the Resource operation type
   * @return returns a MixedOperation object with which you can do basic resource operations. If the class is a known type the
   *         dsl operation logic will be used.
   */
  <T extends HasMetadata, L extends KubernetesResourceList<T>, R extends Resource<T>> MixedOperation<T, L, R> resources(
      Class<T> resourceType, Class<L> listClass, Class<R> resourceClass);

  /**
   * Typed API for managing resources. Any properly annotated POJO can be utilized as a resource.
   *
   * <p>
   * Note: your resource POJO (T in this context) must implement
   * {@link io.fabric8.kubernetes.api.model.Namespaced} if it is a namespace-scoped resource.
   * </p>
   *
   * @param resourceType Class for resource
   * @param <T> represents resource type. If it's a namespaced resource, it must implement
   *        {@link io.fabric8.kubernetes.api.model.Namespaced}
   * @param <L> represents resource list type
   * @return returns a MixedOperation object with which you can do basic resource operations. If the class is a known type the
   *         dsl operation logic will be used.
   */
  default <T extends HasMetadata, L extends KubernetesResourceList<T>> MixedOperation<T, L, Resource<T>> resources(
      Class<T> resourceType, Class<L> listClass) {
    return resources(resourceType, listClass, null);
  }
}
