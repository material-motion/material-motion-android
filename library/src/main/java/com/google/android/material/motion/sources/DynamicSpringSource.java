/*
 * Copyright 2016-present The Material Motion Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.material.motion.sources;

import android.annotation.SuppressLint;
import android.support.animation.DynamicAnimation;
import android.support.animation.DynamicAnimation.ViewProperty;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.support.v4.util.SimpleArrayMap;
import android.util.Property;
import android.view.View;

import com.google.android.indefinite.observable.IndefiniteObservable.Subscription;
import com.google.android.material.motion.MotionBuilder;
import com.google.android.material.motion.MotionObserver;
import com.google.android.material.motion.MotionObserver.SimpleMotionObserver;
import com.google.android.material.motion.ReactiveProperty;
import com.google.android.material.motion.ReactiveProperty.PropertyReactiveProperty;
import com.google.android.material.motion.interactions.MaterialSpring;
import com.google.android.material.motion.properties.ViewProperties;
import com.google.android.material.motion.properties.ViewProperties.DerivativeProperty;

import java.util.HashSet;
import java.util.Set;

/**
 * A source for physics springs.
 */
public final class DynamicSpringSource<T> extends SpringSource<T> {

  public static final System SYSTEM = new System() {
    @Override
    public <T> SpringSource<T> create(MaterialSpring<?, T> spring) {
      return new DynamicSpringSource<>(spring);
    }
  };

  private final MaterialSpring<?, T> interaction;

  private final DynamicSpringBuilder<T> builder;
  private final Set<MotionObserver<T>> observers = new HashSet<>();

  private Subscription destinationSubscription;
  private Subscription frictionSubscription;
  private Subscription tensionSubscription;

  private boolean initialized;

  public DynamicSpringSource(MaterialSpring<?, T> interaction) {
    super(interaction);
    this.interaction = interaction;
    builder = new DynamicSpringBuilder<>(interaction);
  }

  @Override
  protected void onConnect(MotionObserver<T> observer) {
    observers.add(observer);
  }

  @Override
  protected void onEnable() {
    initialized = false;

    tensionSubscription = interaction.tension.subscribe(new SimpleMotionObserver<Float>() {
      @Override
      public void next(Float value) {
        startBuild();
      }
    });
    frictionSubscription = interaction.friction.subscribe(new SimpleMotionObserver<Float>() {
      @Override
      public void next(Float value) {
        startBuild();
      }
    });
    destinationSubscription = interaction.destination.subscribe(new SimpleMotionObserver<T>() {
      @Override
      public void next(T value) {
        startBuild();
      }
    });

    initialized = true;
    startBuild();
  }

  private void startBuild() {
    if (initialized) {
      for (MotionObserver<T> observer : observers) {
        //noinspection unchecked
        observer.build(
          builder,
          interaction.initialValue.read(),
          interaction.initialVelocity.read(),
          interaction.destination.read());
      }
    }
  }

  @Override
  protected void onDisable() {
    if (tensionSubscription != null) {
      tensionSubscription.unsubscribe();
      frictionSubscription.unsubscribe();
      destinationSubscription.unsubscribe();
    }

    builder.stop();
  }

  @Override
  protected void onDisconnect(MotionObserver<T> observer) {
    observers.remove(observer);
  }

  private static class DynamicSpringBuilder<T> extends MotionBuilder<T> {

    private final MaterialSpring<?, T> interaction;
    private final SimpleArrayMap<Key, SpringAnimation> animations = new SimpleArrayMap<>();

    public DynamicSpringBuilder(MaterialSpring<?, T> interaction) {
      this.interaction = interaction;
    }

    @Override
    public void start(ReactiveProperty<T> property, T[] values) {
      float[] initialValues = new float[interaction.vectorizer.getVectorLength()];
      float[] initialVelocities = new float[interaction.vectorizer.getVectorLength()];
      float[] destinations = new float[interaction.vectorizer.getVectorLength()];

      interaction.vectorizer.vectorize(values[0], initialValues);
      interaction.vectorizer.vectorize(values[1], initialVelocities);
      interaction.vectorizer.vectorize(values[2], destinations);

      startAnimations(
        (PropertyReactiveProperty<View, T>) property,
        initialValues,
        initialVelocities,
        destinations);
    }

    private void startAnimations(
      PropertyReactiveProperty<View, T> reactiveProperty,
      float[] initialValues,
      float[] initialVelocities,
      float[] destinations) {
      View target = reactiveProperty.target;
      Property<View, T> property = reactiveProperty.property;

      if (property instanceof DerivativeProperty) {
        DerivativeProperty<View, T> derivative = (DerivativeProperty<View, T>) property;
        for (int i = 0; i < derivative.vectorizer.getVectorLength(); i++) {
          startAnimation(
            target,
            derivative.properties[i],
            derivative.setterTransformation(target, initialValues[i]),
            derivative.setterTransformation(target, initialVelocities[i]),
            derivative.setterTransformation(target, destinations[i]));
        }
      } else if (property.getType() == Float.class) {
        //noinspection unchecked
        startAnimation(
          target,
          (Property<View, Float>) property,
          initialValues[0],
          initialVelocities[0],
          destinations[0]);
      } else {
        throw new IllegalArgumentException("Property not supported: " + property);
      }
    }

    @SuppressLint("NewApi")
    private void startAnimation(
      View target,
      Property<View, Float> property,
      float initialValue,
      float initialVelocity,
      float destination) {
      ViewProperty viewProperty;

      if (property == View.TRANSLATION_X) {
        viewProperty = DynamicAnimation.TRANSLATION_X;
      } else if (property == View.TRANSLATION_Y) {
        viewProperty = DynamicAnimation.TRANSLATION_Y;
      } else if (property == View.TRANSLATION_Z) {
        viewProperty = DynamicAnimation.TRANSLATION_Z;
      } else if (property == View.SCALE_X) {
        viewProperty = DynamicAnimation.SCALE_X;
      } else if (property == View.SCALE_Y) {
        viewProperty = DynamicAnimation.SCALE_Y;
      } else if (property == View.ROTATION) {
        viewProperty = DynamicAnimation.ROTATION;
      } else if (property == View.ROTATION_X) {
        viewProperty = DynamicAnimation.ROTATION_X;
      } else if (property == View.ROTATION_Y) {
        viewProperty = DynamicAnimation.ROTATION_Y;
      } else if (property == View.X) {
        viewProperty = DynamicAnimation.X;
      } else if (property == View.Y) {
        viewProperty = DynamicAnimation.Y;
      } else if (property == View.Z) {
        viewProperty = DynamicAnimation.Z;
      } else if (property == View.ALPHA) {
        viewProperty = DynamicAnimation.ALPHA;
      } else if (property == ViewProperties.SCROLL_X) {
        viewProperty = DynamicAnimation.SCROLL_X;
      } else if (property == ViewProperties.SCROLL_Y) {
        viewProperty = DynamicAnimation.SCROLL_Y;
      } else {
        throw new IllegalArgumentException("Property not supported: " + property);
      }

      Key key = new Key(target, property);
      SpringAnimation animation = animations.get(key);

      if (animation == null) {
        animation = new SpringAnimation(target, viewProperty);
        animation.setStartValue(initialValue);
        animation.setStartVelocity(initialVelocity);
      }

      float stiffness = stiffnessFromOrigamiValue(interaction.tension.read());
      float dampingRatio = dampingRatioFromOrigamiValue(stiffness, interaction.friction.read());

      SpringForce force = new SpringForce(destination);
      force.setStiffness(stiffness);
      force.setDampingRatio(dampingRatio);
      animation.setSpring(force);

      animation.animateToFinalPosition(destination);

      animations.put(key, animation);
    }

    @Override
    public void stop() {
      if (animations.isEmpty()) {
        return;
      }

      for (int i = 0, count = animations.size(); i < count; i++) {
        animations.valueAt(i).cancel();
      }
      animations.clear();
    }

    public static float stiffnessFromOrigamiValue(float value) {
      return value == 0f ? 0f : (value - 30f) * 3.62f + 194f;
    }

    public static float dampingRatioFromOrigamiValue(float stiffness, float value) {
      float friction = value == 0f ? 0f : (value - 8f) * 3f + 25f;
      return (float) (friction / (2 * Math.sqrt(stiffness)));
    }

    private static class Key {

      private View target;
      private Property<View, Float> property;

      public Key(View target, Property<View, Float> property) {
        this.target = target;
        this.property = property;
      }

      @Override
      public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Key key = (Key) o;

        if (!target.equals(key.target)) return false;
        return property.equals(key.property);
      }

      @Override
      public int hashCode() {
        int result = target.hashCode();
        result = 31 * result + property.hashCode();
        return result;
      }
    }
  }
}
