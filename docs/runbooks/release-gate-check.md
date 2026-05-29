You are performing a pre-release gate check for RedisCache-SpringBoot.
A release is only allowed when ALL gates below are green.

## Steps to execute

### Gate 1 – CI Status

```bash
# Check latest CI run on main
gh run list --branch main --limit 5 --json status,conclusion,name,createdAt \
  --jq '.[] | "\(.name) | \(.status) | \(.conclusion) | \(.createdAt)"'
```

- ✅ PASS: latest run for CI workflow is `completed / success`
- ❌ FAIL: any `failure` or `in_progress` → STOP, report the failing job

### Gate 2 – E2E (Karate) locally green

```bash
mvn clean verify -Dspring.profiles.active=build 2>&1 | tail -20
```

- ✅ PASS: `BUILD SUCCESS`, no Karate failures
- ❌ FAIL: → STOP, report failing scenarios

### Gate 3 – No uncommitted changes

```bash
git status --porcelain
```

- ✅ PASS: empty output
- ❌ FAIL: list dirty files and stop

### Gate 4 – Version bump

1. Read current version in `pom.xml` (line with `<version>`).
2. Check existing tags: `git tag --sort=-v:refname | head -5`
3. Confirm the `pom.xml` version is HIGHER than the latest tag.
4. Confirm version follows SemVer (`MAJOR.MINOR.PATCH`).

- ✅ PASS: version is new and valid
- ❌ FAIL: same version already tagged → ask user for new version

### Gate 5 – Release notes

Check if there is a release notes entry or changelog for this version:

- Look for a `CHANGELOG.md` or a release notes section in `README.md`.
- If missing, generate a draft from `git log <last-tag>..HEAD --oneline`.

## Output

If ALL gates pass, output the exact commands to release:

```bash
# Tag the release
git tag -a v<VERSION> -m "Release v<VERSION>

<summary of changes from git log>

# Push tag (triggers release workflow)
git push origin v<VERSION>
```

Then remind the user:

- The `release.yml` workflow will fire automatically on tag push.
- Monitor with: `gh run list --branch v<VERSION> --limit 3`

If ANY gate fails, stop after the first failure and report clearly:

```
❌ Gate <N> FAILED — <reason>
Action required: <what to fix>
```

Do NOT push or tag if any gate is red.
