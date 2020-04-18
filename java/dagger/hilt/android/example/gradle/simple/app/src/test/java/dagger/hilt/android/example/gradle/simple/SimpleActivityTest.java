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
import dagger.Module;
import dagger.Provides;
import dagger.hilt.GenerateComponents;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;
import dagger.hilt.android.testing.AndroidRobolectricEntryPoint;
import dagger.hilt.android.testing.HiltRobolectricTestRule;
import dagger.hilt.android.testing.UninstallModules;
import javax.inject.Inject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/** A simple test using Hilt. */
@UninstallModules(UserNameModule.class)
@GenerateComponents
@AndroidRobolectricEntryPoint
@RunWith(RobolectricTestRunner.class)
// Robolectric requires Java9 to run API 29 and above, so use API 28 instead
@Config(sdk = Build.VERSION_CODES.P, application = SimpleActivityTest_Application.class)
public final class SimpleActivityTest {
  private static final String FAKE_USER_NAME = "FakeUser";
  private static final int TEST_VALUE = 9;

  @Module
  @InstallIn(ApplicationComponent.class)
  interface TestModule {
    @Provides
    @UserName
    static String provideFakeUserName() {
      return FAKE_USER_NAME;
    }

    @Provides
    static int provideInt() {
      return TEST_VALUE;
    }
  }

  @Rule public HiltRobolectricTestRule rule = new HiltRobolectricTestRule();

  @Inject Integer intValue;

  // TODO(user): Add @BindValue

  @Test
  public void testInject() throws Exception {
    assertThat(intValue).isNull();

    SimpleActivityTest_Application.get().inject(this);

    assertThat(intValue).isNotNull();
    assertThat(intValue).isEqualTo(TEST_VALUE);
  }

  @Test
  public void testActivityInject() throws Exception {
    SimpleActivity activity = Robolectric.setupActivity(SimpleActivity.class);
    assertThat(activity.greeter.greet())
        .isEqualTo("Hello, FakeUser!");
  }
}