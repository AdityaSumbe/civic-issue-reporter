# System Architecture

## Architecture Style

The Civic Issue Reporter uses a modular monolithic architecture.

## Components

- React.js frontend
- Spring Boot REST API
- PostgreSQL database
- Cloudinary for image storage
- Leaflet/OpenStreetMap for geographic visualization

## Backend Layers

Controller → Service → Repository → PostgreSQL

## Security

Spring Security and JWT-based authentication provide authentication and role-based authorization.

## Roles

- CITIZEN
- OFFICER
- ADMIN

## Core Flow

React frontend communicates with Spring Boot through REST APIs.

Spring Security validates JWT tokens and authorizes requests based on user roles.

Controllers receive requests and delegate business operations to services.

Services contain application business logic and interact with repositories.

Repositories communicate with PostgreSQL using Spring Data JPA.