# SEP Q3 2022 - Group 2 - Lukos

## File structure

The project is divided into two main parts; the back-end and the front-end. They are represented in their respective directories `backend` and `frontend`. The `backend` directory contains an overarching Maven project, which is split up into the `controller` and `model` packages which are also Maven projects. The `frontend` directory contains an NPM project. The `ci` directory contains various files and scripts necessary for continuous integration during development. The root of the project contains some CI files required to be in the root alongside ignore files to prevent tracking things like external libraries or build artifacts with Git. The file structure does not contain any generated or pre-compiled files, so all files are either source code, testing code or static resources.

### Back-end

All `.java` files in the `backend` directory represent a single class, enum, record or interface. All test related files are nested in `backend/**/src/test` directories and all static resource files are nested in `backend/**/src/**/resources` directories. All other files are considered source code. Thus the files to be checked for the back-end are the following files `backend/**/src/main/java/**/*.java`.

### Front-end

All `.ts` or `.tsx` files in the `frontend` directory represent a single module. All test files are placed alongside the modules they are testing and have the format `*.test.ts`. All static resource files are in the `frontend/src/static` and `frontend/public` directories. All files directly in the root of `frontend` are configuration files or raw HTML. All `.ts` and `.tsx` files in `frontend/src` that are not test files are considered source code. Thus the files to be checked for the front-end are the following files `frontend/src/**/*.{ts,tsx}` with exception of the following files `frontend/src/**/*.test.{ts,tsx}`.
