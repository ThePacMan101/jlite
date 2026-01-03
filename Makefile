# Simple Java project Makefile (best practices)
# - Compiles sources in `src/` to `build/`
# - Packages runnable jar in `dist/` with Main-Class `lite.Lite`
# - Provides `run`, `run-args`, `clean`, and `help` targets

# Tools (overridable)
JAVA  ?= java
JAVAC ?= javac
JFLAGS ?= -Xlint:all -encoding UTF-8

# Project layout
PROJECT   := jlite
SRC_DIR   := src
BUILD_DIR := build
DIST_DIR  := dist
MAIN_CLASS := lite.Lite
JAR       := $(DIST_DIR)/$(PROJECT).jar

SOURCES := $(shell find $(SRC_DIR) -name "*.java")
CLASSPATH := $(BUILD_DIR)

BIN_DIR	?= $(HOME)/.local/bin

.PHONY: jar compile clean help install uninstall generate

# Default goal builds the jar
.DEFAULT_GOAL := jar

# Generate AST/visitor classes from script
generate:
	@echo "Generating AST classes..."
	@python3 scripts/generate_ast_classes.py $(SRC_DIR)/lite

# Compile all sources to classes under build/
compile: generate
	@mkdir -p $(BUILD_DIR)
	$(JAVAC) $(JFLAGS) -d $(BUILD_DIR) -cp $(CLASSPATH) -sourcepath $(SRC_DIR) $(SOURCES)

# Package classes into a runnable jar
jar: compile
	@mkdir -p $(DIST_DIR)
	@echo "Main-Class: $(MAIN_CLASS)" > $(BUILD_DIR)/MANIFEST.MF
	jar cfm $(JAR) $(BUILD_DIR)/MANIFEST.MF -C $(BUILD_DIR) .

# Clean build and distribution artifacts
clean:
	rm -rf $(BUILD_DIR) $(DIST_DIR)

# install jlite
install: jar
	@mkdir -p $(BIN_DIR)
	@echo '#!/usr/bin/env bash' > $(BIN_DIR)/jlite
	@echo 'exec rlwrap $(JAVA) -jar "$(abspath $(JAR))" "$$@"' >> $(BIN_DIR)/jlite
	@chmod +x $(BIN_DIR)/jlite
	@echo "Installed: $(BIN_DIR)/jlite"
	@if ! grep -q '$$HOME/.local/bin' ~/.profile; then \
		echo 'export PATH="$$HOME/.local/bin:$$PATH"' >> ~/.profile; \
		echo "Added ~/.local/bin to PATH in ~/.profile"; \
	else \
		echo "PATH already set in ~/.profile"; \
	fi

uninstall:
	@rm -f $(BIN_DIR)/jlite
	@sed -i.bak '/\.local\/bin/d' ~/.profile && rm -f ~/.profile.bak
	@echo "Removed: $(BIN_DIR)/jlite and PATH from ~/.profile"

# Friendly help output
help:
	@echo "Targets:"
	@echo "  generate       Generate AST classes from script"
	@echo "  jar            Build runnable jar (default)"
	@echo "  compile        Compile sources to classes"
	@echo "  install        Install wrapper script to $(BIN_DIR)"
	@echo "  uninstall      Remove wrapper script"
	@echo "  clean          Remove build and dist"
