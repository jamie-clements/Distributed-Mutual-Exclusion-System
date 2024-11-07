# Distributed Mutual Exclusion (DME) System

A Java implementation of a distributed mutual exclusion system that manages access to shared resources across multiple nodes using a coordinator-based token mechanism.

## Project Overview

This project implements a distributed mutual exclusion system with advanced features including priority-based request handling, starvation prevention, and fault tolerance. 

## Key Features

- **Token-Based Mutual Exclusion**: Ensures only one node can access critical sections at a time
- **Priority Queue Management**: Handles access requests based on priority levels
- **Starvation Prevention**: Implements request aging to prevent low-priority requests from being indefinitely delayed
- **Fault Tolerance**: Handles coordinator crashes with exponential backoff reconnection
- **System Monitoring**: Comprehensive logging system for tracking system events
- **Graceful Shutdown**: Supports system-wide shutdown initiated by nodes

## System Architecture

### High-Level Overview
                ┌─────────────────┐
                │   Coordinator   │
                └────────┬────────┘
                         │
                ┌────────┴────────┐
                │   Token Queue   │
                └────────┬────────┘
                         │
        ┌────────────────┼────────────────┐
        │                │                │
  ┌─────┴─────┐   ┌─────┴─────┐   ┌─────┴─────┐
  │   Node 1   │   │   Node 2   │   │   Node 3   │
  └───────────┘   └───────────┘   └───────────┘

## Component Details

### 1. Coordinator System
#### Coordinator (`Coordinator.java`)
- **Purpose**: Main controller of the DME system
- **Responsibilities**:
  - Initializes system components
  - Manages token distribution
  - Coordinates system shutdown
- **Key Methods**:
  - `main()`: System entry point and initialization

#### Request Buffer (`C_buffer.java`)
- **Purpose**: Manages request queue with priority ordering
- **Key Features**:
  - Priority-based queuing
  - Request aging mechanism
  - Thread synchronization
- **Key Methods**:
  - `saveRequest()`: Adds new requests to queue
  - `get()`: Retrieves highest priority request
  - `ageRequests()`: Prevents starvation

#### Token Manager (`C_mutex.java`)
- **Purpose**: Controls token distribution and collection
- **Responsibilities**:
  - Token granting
  - Token collection
  - Critical section access control
- **Key Methods**:
  - `run()`: Token management loop
  - Token granting and collection handlers

#### Request Receiver (`C_receiver.java`)
- **Purpose**: Handles incoming node connections
- **Features**:
  - Multi-threaded request handling
  - Connection management
- **Key Methods**:
  - `run()`: Connection acceptance loop

#### Connection Handler (`C_Connection_r.java`)
- **Purpose**: Processes individual node requests
- **Features**:
  - Request parsing
  - Buffer interaction
  - Shutdown request handling
- **Key Methods**:
  - `run()`: Request processing
  - `initiateShutdown()`: System shutdown handling

### 2. Node System
#### Node (`Node.java`)
- **Purpose**: Client process requesting critical section access
- **Features**:
  - Token request mechanism
  - Critical section execution
  - Fault tolerance
  - Coordinator crash handling
- **Key Methods**:
  - Token request/return handlers
  - `handleCoordinatorDown()`: Fault tolerance
  - Critical section execution logic

### 3. Supporting Components
#### Request (`Request.java`)
- **Purpose**: Encapsulates request information
- **Features**:
  - Priority management
  - Timestamp tracking
  - Comparison logic
- **Key Methods**:
  - `compareTo()`: Priority-based comparison
  - Getters for request attributes

#### Logger (`Logger.java`)
- **Purpose**: System-wide event logging
- **Features**:
  - Singleton pattern
  - Thread-safe logging
  - Structured event format
- **Key Methods**:
  - `logEvent()`: Event logging
  - `clearLogFile()`: Log management

## Getting Started

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- Network connectivity for distributed setup
