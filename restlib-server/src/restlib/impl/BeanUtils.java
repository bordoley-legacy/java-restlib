/*
 * Copyright (C) 2012 David Bordoley
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package restlib.impl;

import static java.util.Locale.ENGLISH;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public final class BeanUtils {
    // From java.beans.NameGenerator
    private static String capitalize(final String name) {
        if ((name == null) || (name.length() == 0)) {
            return name;
        }
        return name.substring(0, 1).toUpperCase(ENGLISH) + name.substring(1);
    }

    public static Object getProperty(final Object obj,
            final String param)
                            throws
            IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        Preconditions.checkNotNull(obj);
        Preconditions.checkNotNull(param);

        final Method[] methods = obj.getClass().getMethods();

        // FIXME: Boolean getter support
        final String methodName = "get" + capitalize(param);
        for (final Method method : methods) {
            method.setAccessible(true);
            if (method.getName().equals(methodName) &&
                    (method.getParameterTypes().length == 0)) {
                return method.invoke(obj);
            }
        }

        throw new NoSuchMethodException();
    }

    public static Iterable<String> getPropertyAsIterable(final Object obj,
            final String param)
            throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        final Object result = getProperty(obj, param);

        if (result.getClass().isAssignableFrom(Iterable.class)) {
            return Iterables.transform(
                    (Iterable<?>) result,
                    new Function<Object, String>() {
                        public String apply(final Object obj) {
                            return obj.toString();
                        }
                    });
        } else {
            return ImmutableList.of(result.toString());
        }
    }

    public static String getPropertyAsString(final Object obj,
            final String param) throws IllegalArgumentException,
            IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        return getProperty(obj, param).toString();
    }

    public static <T> T mapToObject(final Map<String, String> map,
            final Class<T> clss) throws InstantiationException,
            IllegalAccessException {
        Preconditions.checkNotNull(map);
        Preconditions.checkNotNull(clss);

        final T obj = clss.newInstance();
        BeanUtils.populateObject(map, obj);

        return obj;
    }

    @SuppressWarnings("unchecked")
    public static <T> T mapToObject(
            final Map<String, String> map,
            final T prototype) throws InstantiationException,
            IllegalAccessException {
        Preconditions.checkNotNull(prototype);
        return (T) mapToObject(map, prototype.getClass());
    }

    public static <T> T multiMapToObject(
            final Multimap<String, String> multimap,
            final Class<T> clss) {
        Preconditions.checkNotNull(clss);

        final T obj;
        try {
            obj = clss.newInstance();
        } catch (final InstantiationException e) {
            throw new RuntimeException(e);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        BeanUtils.populateObject(multimap, obj);
        return obj;
    }

    @SuppressWarnings("unchecked")
    public static <T> T multiMapToObject(
            final Multimap<String, String> multimap,
            final T prototype) {
        Preconditions.checkNotNull(prototype);
        return (T) multiMapToObject(multimap, prototype.getClass());
    }

    public static void populateObject(
            final Map<String, String> map,
            final Object obj) {
        Preconditions.checkNotNull(map);
        Preconditions.checkNotNull(obj);
        
        for (final Entry<String, String> entry : map.entrySet()) {
            try {
                BeanUtils.setProperty(obj,
                        entry.getKey(),
                        ImmutableList.of(entry.getValue()));
            } catch (final IllegalAccessException e) {
            } catch (final InvocationTargetException e) {
            } catch (final IllegalArgumentException e) {
            } catch (final NoSuchMethodException e) {
            }
        }
    }

    public static void populateObject(
            final Multimap<String, String> multimap,
            final Object obj) {
        Preconditions.checkNotNull(multimap);
        Preconditions.checkNotNull(obj);
        
        for (final Entry<String, Collection<String>> entry : multimap.asMap()
                .entrySet()) {
            try {
                BeanUtils.setProperty(obj, entry.getKey(), entry.getValue());
            } catch (final IllegalAccessException e) {
            } catch (final InvocationTargetException e) {
            } catch (final IllegalArgumentException e) {
            } catch (final NoSuchMethodException e) {
            }
        }
    }

    public static void setProperty(
            final Object obj,
            final String param,
            final Iterable<String> value)
                            throws IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        Preconditions.checkNotNull(obj);
        Preconditions.checkNotNull(param);
        Preconditions.checkNotNull(value);

        if (Iterables.size(value) == 0) {
            return;
        }

        final Method[] methods = obj.getClass().getMethods();
        final String methodName = "set" + capitalize(param);

        // FIXME: Should we depend upon autoboxing?
        for (final Method method : methods) {
            method.setAccessible(true);
            if (method.getName().equals(methodName) &&
                    (method.getParameterTypes().length == 1)) {
                final Class<?> argClass = method.getParameterTypes()[0];

                if (argClass.equals(value.getClass())) {
                    method.invoke(obj, value);
                    return;
                } else if (argClass.equals(String.class)) {
                    method.invoke(obj, Iterables.getFirst(value, ""));
                    return;
                } else if (argClass.equals(Boolean.class) ||
                    argClass.equals(boolean.class)) {
                    method.invoke(obj,
                            Boolean.getBoolean(
                                    Iterables.getFirst(value, "true")));
                    return;
                } else if (argClass.equals(Integer.class) ||
                    argClass.equals(int.class)) {
                    method.invoke(obj,
                            Integer.valueOf(
                                    Iterables.getFirst(value, "0")));
                    return;
                } else if (argClass.equals(Long.class) ||
                    argClass.equals(long.class)) {
                    method.invoke(obj,
                            Long.valueOf(
                                    Iterables.getFirst(value, "0")));
                    return;
                } else if (argClass.equals(Float.class) ||
                    argClass.equals(float.class)) {
                    method.invoke(obj,
                            Float.valueOf(
                                    Iterables.getFirst(value, "0")));
                    return;
                } else if (argClass.equals(Double.class) ||
                    argClass.equals(double.class)) {
                    method.invoke(obj,
                            Double.valueOf(
                                    Iterables.getFirst(value, "0")));
                    return;
                } else if (argClass.isAssignableFrom(List.class)) {
                    method.invoke(obj,
                             Lists.newLinkedList(value));
                } else if (argClass.isAssignableFrom(Set.class)) {
                    method.invoke(obj,
                            Sets.newLinkedHashSet(value));
                }
            }
        }

        throw new NoSuchMethodException();
    }

    private BeanUtils() {
    }
}
