// @ts-check
const { defineConfig } = require('@playwright/test');

module.exports = defineConfig({
  testDir: './e2e',
  timeout: 120 * 1000, // 120 seconds
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 0,
  workers: process.env.CI ? 1 : undefined,
  reporter: 'html',
  use: {
    baseURL: 'http://localhost:8080',
    trace: 'on-first-retry',
  },

  projects: [
    {
      name: 'chromium',
      use: { ...require('@playwright/test').devices['Desktop Chrome'] },
    },
  ],

  webServer: {
    command: 'java -jar target/edujob_app_track-0.0.1-SNAPSHOT.jar > /home/koxiperu/.gemini/tmp/8512cec54df2c03531b6941afb1094fece24edf5171184ace221612686f0587b/playwright-app.log 2>&1',
    url: 'http://localhost:8080',
    timeout: 120 * 1000, // Wait for 2 minutes
    reuseExistingServer: !process.env.CI,
  },
});
