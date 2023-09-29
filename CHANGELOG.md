# Change Log
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## [1.1.2 - 2023-09-29]
### Added
- Java Doc Plugin

## [1.1.1 - 2023-09-28]
### Added
- Nexus Staging Plugin
- Apache License in pom.xml

## [1.1.0 - 2023-09-28]
### Added
- Gitignore
- Dockerignore
- build.yml
- Dockerfile
- Exporter Client
- Download export
- Unzip files
- Templates
- Thymeleaf support
- Variables Replacer
- Exporter configuration in template
- Exporter template in quality report template (optional)
- Template in request
- Thymeleaf Engine
- Groovy-Templates Engine
- Report Generator
- Header format
- External Sheet Utils
- Report Meta Info File 
- Download Report
- Remove temporal files
- Report Status
- Context and CellContext for Engines
- Report examples
- Logs for report generation
- Multilevel comparable
- Request logs
- Exporter logs
- Export URL in Request
- HTTP Request and Connection Timeout
- HTTP TCP configuration
- Not validated
- CORS support
- Report Meta Info Paging
- Get all Template IDs
- Download report template
- Split Excel file if too large and zip
- Numeric and string cell values
- Running Report Manager
- Clear Logger Buffer
- Sort Source Paths in Context
- Manage Exception during report generation
- Custom Template ID
- Deployment in Maven Central Repository
- GPG Sign


### Changed
- Project name: quality-report-generator to reporter
- Bugfix: Exporter Template
- Bugfix: Patients pro Attribute
- Bugfix: Create sheet if it does not exist

### Fixed
- Intellij Warnings
- Retries of webclient managed by webclient retrywhen avoiding stackoverflow by recursive function
