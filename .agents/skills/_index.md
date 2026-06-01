---
description: Universal AI agent skills for the Muse project
---

# Skills Index

This directory contains universal skills usable by any AI agent (Claude, Gemini, Cursor, Copilot, etc.).

## Available Skills

| Skill | Description |
|-------|-------------|
| [muse-release-manager](muse-release-manager/SKILL.md) | Release process management |

## Usage

Each skill is defined in a `SKILL.md` file with YAML frontmatter. Agents can load skills by reading the SKILL.md file directly.

### Adding New Skills

1. Create a directory under `.agents/skills/<skill-name>/`
2. Add a `SKILL.md` with frontmatter (`name`, `description`, `agents`)
3. Add any reference materials in `references/`
4. Update this index

### Agent Configuration

**Gemini** - Add to `.gemini/settings.json`:
```json
{
  "skills": ".agents/skills"
}
```

**Claude Code** - Reference in `CLAUDE.md`:
```markdown
## Skills
Universal skills are in `.agents/skills/`. Load SKILL.md for workflow instructions.
```

**Cursor** - Reference in `.cursorrules`:
```
Skills are in .agents/skills/. Read SKILL.md for available workflows.
```
