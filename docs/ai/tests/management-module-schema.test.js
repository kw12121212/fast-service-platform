/**
 * Unit tests for descriptor-driven-management-module.schema.json
 * Validates workflow-enabled descriptors, boundary constraints, and backward compatibility.
 *
 * Run: node docs/ai/tests/management-module-schema.test.js
 */

const Ajv2020 = require('ajv/dist/2020');
const fs = require('fs');
const path = require('path');

let ajv;
try {
  ajv = new Ajv2020();
} catch {
  // Fallback: try resolving from global node_modules
  const homeDir = require('os').homedir();
  const nvmDir = path.join(homeDir, '.nvm/versions/node', process.version);
  const AjvFallback = require(path.join(nvmDir, 'lib/node_modules/@google/gemini-cli/node_modules/ajv/dist/2020'));
  ajv = new AjvFallback();
}

const schemaPath = path.resolve(__dirname, '../schemas/descriptor-driven-management-module.schema.json');
const schema = JSON.parse(fs.readFileSync(schemaPath, 'utf8'));

const fixturesDir = path.resolve(__dirname, '../management-modules');

function loadDescriptor(name) {
  return JSON.parse(JSON.stringify(JSON.parse(fs.readFileSync(path.join(fixturesDir, name), 'utf8'))));
}

let passed = 0;
let failed = 0;

function test(description, fn) {
  try {
    fn();
    console.log(`  PASS: ${description}`);
    passed++;
  } catch (err) {
    console.log(`  FAIL: ${description}`);
    console.log(`        ${err.message}`);
    failed++;
  }
}

function assert(condition, message) {
  if (!condition) throw new Error(message || 'Assertion failed');
}

console.log('\nManagement-Module Schema Validation Tests\n');

// (a) workflow-enabled descriptor passes
test('workflow-enabled descriptor (leave-request) passes validation', () => {
  const data = loadDescriptor('leave-request.management-module.json');
  const valid = ajv.validate(schema, data);
  assert(valid, 'leave-request should pass: ' + JSON.stringify(ajv.errors));
  assert(data.managementModule.workflow, 'should have workflow section');
  assert(data.boundaries.usesWorkflowGeneration === true, 'usesWorkflowGeneration should be true');
  assert(data.generatedModuleInputs.frontend.workflowComponentId === 'workflow-panel', 'should have workflowComponentId');
});

// (b) descriptor with workflow but usesWorkflowGeneration: false fails
test('descriptor with workflow but usesWorkflowGeneration: false fails validation', () => {
  const data = loadDescriptor('leave-request.management-module.json');
  data.boundaries.usesWorkflowGeneration = false;
  const valid = ajv.validate(schema, data);
  assert(!valid, 'should fail when workflow present but usesWorkflowGeneration is false');
});

// (c) descriptor without workflow but usesWorkflowGeneration: true fails
test('descriptor without workflow but usesWorkflowGeneration: true fails validation', () => {
  const data = loadDescriptor('department-directory.management-module.json');
  data.boundaries.usesWorkflowGeneration = true;
  const valid = ajv.validate(schema, data);
  assert(!valid, 'should fail when no workflow but usesWorkflowGeneration is true');
});

// (d) existing descriptors without workflow still pass
test('department-directory (no workflow) still passes validation', () => {
  const data = loadDescriptor('department-directory.management-module.json');
  const valid = ajv.validate(schema, data);
  assert(valid, 'department-directory should still pass: ' + JSON.stringify(ajv.errors));
});

test('department-overview (no workflow) still passes validation', () => {
  const data = loadDescriptor('department-overview.management-module.json');
  const valid = ajv.validate(schema, data);
  assert(valid, 'department-overview should still pass: ' + JSON.stringify(ajv.errors));
});

// Additional: workflow without workflowComponentId in frontend inputs should fail
test('workflow-enabled descriptor missing workflowComponentId fails validation', () => {
  const data = loadDescriptor('leave-request.management-module.json');
  delete data.generatedModuleInputs.frontend.workflowComponentId;
  const valid = ajv.validate(schema, data);
  assert(!valid, 'should fail when workflow present but workflowComponentId missing');
});

// Additional: invalid workflow action type fails
test('workflow descriptor with invalid action type fails validation', () => {
  const data = loadDescriptor('leave-request.management-module.json');
  data.managementModule.workflow.actions.push({ action: 'escalate', label: 'Escalate' });
  const valid = ajv.validate(schema, data);
  assert(!valid, 'should fail with unsupported action type');
});

console.log(`\nResults: ${passed} passed, ${failed} failed, ${passed + failed} total\n`);
process.exit(failed > 0 ? 1 : 0);
