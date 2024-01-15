module.exports = {
  e2e: {
    setupNodeEvents(on, config) {
      // implement node event listeners here
    },
    baseUrl: 'http://localhost:4200/',
    testIsolation: true,
    supportFile: 'cypress/support/e2e.js'
  },
};
