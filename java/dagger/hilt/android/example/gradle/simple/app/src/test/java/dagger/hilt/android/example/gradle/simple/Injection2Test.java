/*
 * Copyright (C) 2020 The Dagger Authors.
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

package dagger.hilt.android.example.gradle.simple;

import static com.google.common.truth.Truth.assertThat;

import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.GenerateComponents;
import dagger.hilt.InstallIn;
import dagger.hilt.android.AndroidEntryPoint;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.components.ApplicationComponent;
import dagger.hilt.android.testing.AndroidRobolectricEntryPoint;
import dagger.hilt.android.testing.HiltTestRule;
import javax.inject.Inject;
import javax.inject.Named;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

/** Tests basic injection APIs, and that bindings don't conflict with {@link Injection1Test}. */
@GenerateComponents
@AndroidRobolectricEntryPoint
@RunWith(AndroidJUnit4.class)
// Robolectric requires Java9 to run API 29 and above, so use API 28 instead
@Config(sdk = Build.VERSION_CODES.P, application = Injection2Test_Application.class)
public final class Injection2Test {
  private static final String APPLICATION_QUALIFIER = "APPLICATION_QUALIFIER";
  private static final String ACTIVITY_QUALIFIER = "ACTIVITY_QUALIFIER";
  private static final String APPLICATION_VALUE = "Injection2Test_ApplicationValue";
  private static final String ACTIVITY_VALUE = "Injection2Test_ActivityValue";

  @Module
  @InstallIn(ApplicationComponent.class)
  interface TestApplicationModule {
    @Provides
    @Named(APPLICATION_QUALIFIER)
    static String provideString() {
      return APPLICATION_VALUE;
    }
  }

  @Module
  @InstallIn(ActivityComponent.class)
  interface TestActivityModule {
    @Provides
    @Named(ACTIVITY_QUALIFIER)
    static String provideString() {
      return ACTIVITY_VALUE;
    }
  }

  /** Test activity used to test activity injection */
  // TODO(user): Update to extend non-gen code when test sources are also transformed.
  @AndroidEntryPoint(AppCompatActivity.class)
  public static final class TestActivity extends Hilt_Injection2Test_TestActivity {
    @Inject @Named(ACTIVITY_QUALIFIER) String activityValue;
  }

  @Rule public HiltTestRule rule = new HiltTestRule(this);

  @Inject @Named(APPLICATION_QUALIFIER) String applicationValue;

  @Test
  public void testApplicationInjection() throws Exception {
    assertThat(applicationValue).isNull();
    Injection2Test_Application.get().inject(this);
    assertThat(applicationValue).isEqualTo(APPLICATION_VALUE);
  }

  @Test
  public void testActivityInjection() throws Exception {
    try (ActivityScenario<TestActivity> scenario =
        ActivityScenario.launch(TestActivity.class)) {
      scenario.onActivity(activity -> assertThat(activity.activityValue).isEqualTo(ACTIVITY_VALUE));
    }
  }
}