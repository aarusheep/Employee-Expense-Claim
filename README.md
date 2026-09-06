# Employee Expense Claim System

A desktop application for managing employee expense claims at Chai.co. Employees submit expense claims with supporting proof (image or PDF), and the Finance team reviews, approves, or rejects them through a dashboard. Status updates are communicated to employees by email.

---

## Table of Contents

1. [Overview](#overview)
2. [Features](#features)
3. [Technology Stack](#technology-stack)
4. [Repository Structure](#repository-structure)
5. [Database Schema](#database-schema)
6. [Prerequisites](#prerequisites)
7. [Configuration](#configuration)
8. [Setup and Installation](#setup-and-installation)
9. [Running the Application](#running-the-application)
10. [Security Notes](#security-notes)

---

## Overview

The system has two user-facing roles:

- **Employee** — submits a claim consisting of name, email, expense category, amount, description, and a proof document (image or PDF). On submission, the employee receives a confirmation email.
- **Finance Team** — logs in to a dashboard listing all claims, filterable by status, and approves or rejects each one. The employee receives a status-update email once a decision is made.

All claim data, including the uploaded proof file, is stored in a PostgreSQL database.

---

## Features

- Claim submission with file (image/PDF) attachment stored directly in the database
- Automatic email confirmation on submission
- Finance dashboard to view all claims, or filter by status or employee
- Approve / reject workflow with automatic status-update emails
- Standalone batch utility to (re-)send status update emails for claims already marked Approved or Rejected

---

## Technology Stack

- **Java** (Swing) — desktop application UI
- **PostgreSQL** — claim data storage, including proof file binary data
- **JDBC** (PostgreSQL driver) — database connectivity
- **JavaMail API** — outbound email notifications via SMTP (Gmail)

---

## Repository Structure

```
Employee-Expense-Claim/
├── ExpenseClaimSystem.java   # Main Swing application: employee submission UI and finance dashboard
├── ClaimDAO.java             # Data access layer: insert/read/update claims, fetch proof files, send confirmation email
├── SendStatusEmails.java     # Standalone utility + DB connection class; batch-sends status emails for decided claims
├── SQL ( PostresSql ) create table.   # DDL script to create the `employee` table
└── README.md
```

**Note on structure:** the PostgreSQL connection class (`DB`) is currently defined inside `SendStatusEmails.java` and shared by `ClaimDAO.java` at compile time. If the project grows, consider moving `DB` into its own file for clarity.

---

## Database Schema

The application uses a single table, `employee`, created by the provided SQL script:

| Column | Type | Notes |
|---|---|---|
| `sr_no` | serial, unique | Primary claim identifier |
| `empname` | varchar(100), not null | Employee name |
| `email` | varchar(100), not null | Employee email — used for status notifications |
| `amount` | numeric | Claimed expense amount |
| `proof` | bytea | Proof file contents (image/PDF), stored as binary |
| `proofname` | varchar(200) | Original filename of the proof document |
| `description` | text, not null | Expense description |
| `status` | varchar(20), default `'pending'` | Claim status: Pending / Approved / Rejected |
| `created_at` | timestamp, default `CURRENT_TIMESTAMP` | Submission time |

Run the DDL script (`SQL ( PostresSql ) create table.`) against your database before starting the application.

---

## Prerequisites

- Java Development Kit (JDK) 8 or later
- PostgreSQL 12 or later
- PostgreSQL JDBC driver (`postgresql-<version>.jar`)
- JavaMail API library (`javax.mail.jar`, or the Jakarta Mail equivalent for newer JDKs)
- A Gmail account with an [App Password](https://support.google.com/accounts/answer/185833) generated for SMTP access (standard account passwords will not work with Gmail SMTP)

---

## Configuration

Database and email credentials must **not** be committed to source control. Externalize them — for example, via environment variables read at startup, or a local `config.properties` file excluded via `.gitignore`.

Required configuration values:

| Setting | Used in | Purpose |
|---|---|---|
| Database URL | `DB` class (`SendStatusEmails.java`) | JDBC connection string, e.g. `jdbc:postgresql://localhost:5432/<database>` |
| Database user | `DB` class | PostgreSQL username |
| Database password | `DB` class | PostgreSQL password |
| SMTP sender address | `ClaimDAO.java`, `SendStatusEmails.java` | Gmail address emails are sent from |
| SMTP app password | `ClaimDAO.java`, `SendStatusEmails.java` | Gmail App Password (not the account login password) |

The current source has these values hardcoded directly in `ClaimDAO.java` and `SendStatusEmails.java`. Before any further use of this repository — especially if it remains public — replace the hardcoded database and email credentials with externally supplied configuration, and rotate the existing exposed credentials.

---

## Setup and Installation

1. **Create the database:**
   ```sql
   CREATE DATABASE "Chai.Co";
   ```

2. **Create the schema:**
   ```bash
   psql -U postgres -d "Chai.Co" -f "SQL ( PostresSql ) create table."
   ```

3. **Add required libraries to your classpath:**
   - PostgreSQL JDBC driver
   - JavaMail API (or Jakarta Mail)

4. **Set configuration values** (database URL/credentials, SMTP sender/app password) via your chosen externalized configuration method rather than editing the source directly.

5. **Compile:**
   ```bash
   javac -cp ".:postgresql-<version>.jar:javax.mail.jar" *.java
   ```
   *(On Windows, use `;` instead of `:` as the classpath separator.)*

---

## Running the Application

**Main application (employee submission + finance dashboard):**
```bash
java -cp ".:postgresql-<version>.jar:javax.mail.jar" ExpenseClaimSystem
```

**Status email batch utility** — sends/re-sends notification emails for claims already marked Approved or Rejected. Intended to be run on demand or on a schedule (e.g. via cron or Windows Task Scheduler):
```bash
java -cp ".:postgresql-<version>.jar:javax.mail.jar" SendStatusEmails
```



