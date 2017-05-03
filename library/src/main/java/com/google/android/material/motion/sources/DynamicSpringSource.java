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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
    private List<SpringAnimation> animations;

    public DynamicSpringBuilder(MaterialSpring<?, T> interaction) {
      this.interaction = interaction;
    }

    @Override
    public void start(ReactiveProperty<T> property, T[] values) {
      stop();

      float[] initialValues = new float[interaction.vectorizer.getVectorLength()];
      float[] initialVelocities = new float[interaction.vectorizer.getVectorLength()];
      float[] initialDestinations = new float[interaction.vectorizer.getVectorLength()];

      interaction.vectorizer.vectorize(values[0], initialValues);
      interaction.vectorizer.vectorize(values[1], initialVelocities);
      interaction.vectorizer.vectorize(values[2], initialDestinations);

      PropertyReactiveProperty<View, T> p = (PropertyReactiveProperty<View, T>) property;

      if (p.property instanceof DerivativeProperty) {
        animations = createAnimations(
          p.target,
          (DerivativeProperty<View, T>) p.property,
          initialValues,
          initialVelocities,
          initialDestinations);
      } else if (p.property.getType() == Float.class) {
        //noinspection unchecked
        animations = Collections.singletonList(createAnimation(
          p.target,
          (Property<View, Float>) p.property,
          initialValues[0],
          initialVelocities[0],
          initialDestinations[0]));
      } else {
        throw new IllegalArgumentException("Property not supported: " + p.property);
      }

      for (int i = 0, count = animations.size(); i < count; i++) {
        animations.get(i).start();
      }
    }

    private List<SpringAnimation> createAnimations(
      View target,
      DerivativeProperty<View, T> property,
      float[] initialValues,
      float[] initialVelocities,
      float[] initialDestinations) {
      List<SpringAnimation> animations = new ArrayList<>();

      for (int i = 0; i < property.vectorizer.getVectorLength(); i++) {
        animations.add(createAnimation(
          target,
          property.properties[i],
          property.setterTransformation(target, initialValues[i]),
          property.setterTransformation(target, initialVelocities[i]),
          property.setterTransformation(target, initialDestinations[i])));
      }

      return animations;
    }

    @SuppressLint("NewApi")
    private SpringAnimation createAnimation(
      View target,
      Property<View, Float> property,
      float initialValue,
      float initialVelocity,
      float initialDestination) {
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

      SpringAnimation animation = new SpringAnimation(target, viewProperty, initialDestination);
      animation.setStartValue(initialValue);
      animation.setStartVelocity(initialVelocity);

      float stiffness = stiffnessFromOrigamiValue(interaction.tension.read());
      float dampingRatio = dampingRatioFromOrigamiValue(stiffness, interaction.friction.read());

      SpringForce force = new SpringForce(initialDestination);
      force.setStiffness(stiffness);
      force.setDampingRatio(dampingRatio);
      animation.setSpring(force);

      return animation;
    }

    @Override
    public void stop() {
      if (animations == null) {
        return;
      }

      for (int i = 0, count = animations.size(); i < count; i++) {
        animations.get(i).cancel();
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
  }
}
