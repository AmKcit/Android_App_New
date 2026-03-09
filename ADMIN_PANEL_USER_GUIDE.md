# Admin Panel User Guide

## Welcome to the Modern Admin Dashboard

The Air Quality Predictor Admin Panel has been completely redesigned with modern Material Design principles and enhanced functionality.

## Main Dashboard Features

### 1. Quick Statistics Overview
Located at the top of the dashboard are three statistics cards:
- **Total Users**: Shows the total number of registered users
- **Active Users**: Displays count of currently active users
- **Admins**: Shows number of admin accounts

These statistics update in real-time when you refresh the user list.

### 2. User Management

#### Viewing Users
All registered users are displayed in a modern card-based list. Each user card shows:
- **User Avatar**: Initials in a colored circle
- **Email Address**: User's registered email
- **Role Badge**: Color-coded badge indicating User or Admin role
  - Orange badge = Regular User
  - Red badge = Admin
- **Status Indicator**: Green badge for Active, Red for Inactive
- **Action Buttons**: Edit and Delete options

#### Editing Users
1. Click the **"Edit"** button on any user card
2. A detailed editing screen opens showing:
   - User email
   - Current role (User/Admin)
   - Account status toggle
   - Account creation date
   - Last login date
3. Make your changes:
   - Change role using the dropdown
   - Toggle account status with the switch
4. Click **"Save"** to apply changes or **"Cancel"** to discard

#### Deleting Users
1. Click the **"Delete"** button on any user card
2. A confirmation dialog will appear
3. Click **"Yes"** to confirm deletion or **"No"** to cancel
4. The user and all associated data will be removed

#### Refreshing Users
Click the **"Refresh"** button to reload the user list and update all statistics.

### 3. Analytics Dashboard

Access advanced analytics by clicking the **"Analytics"** button:

#### User Growth
- Visual placeholder for growth trends
- Historical user registration data

#### Activity Statistics
- **Logins This Week**: Number of unique users who logged in this week
- **Average Users Online**: Daily average of concurrent users
- **Peak Time**: Time of day with highest user activity

#### Server Health
- **Server Status**: Current server operational status (🟢 Online/🔴 Offline)
- **Average Response Time**: Mean response time for requests
- **Uptime**: System availability percentage

### 4. Settings

Click the **"Settings"** button to access system configuration:

#### Features
- **Enable Notifications**: Toggle system-wide notifications
- **Maintenance Mode**: Put the system in maintenance mode (users see message)
- **System Information**: View app version and last update timestamp

### 5. Account Actions

#### Available Buttons

**Primary Actions** (Bottom of screen):
- **Analytics**: View comprehensive system analytics (Purple button)
- **Settings**: Configure system settings (Orange button)
- **Logout**: Securely log out from admin account (Red button)

## User Interface Design

### Color System
- **Blue (#1976D2)**: Primary actions and information
- **Green (#388E3C)**: Active status and success states
- **Orange (#FF9800)**: Secondary actions
- **Red (#E74C3C)**: Danger actions and warnings
- **Purple (#673AB7)**: Analytics
- **Gray (#757575)**: Labels and secondary text

### Navigation Flow

```
Login Screen
    ↓
Admin Dashboard (Main Screen)
    ├→ Edit User (Edit button on user card)
    ├→ Analytics Dashboard (Analytics button)
    ├→ Settings (Settings button)
    └→ Logout (Logout button)
```

## Quick Tips

1. **Bulk Updates**: Use Edit to manage multiple user permissions
2. **Monitor Activity**: Check Analytics regularly for system health
3. **Maintenance**: Use Settings for system-wide configurations
4. **Data Safety**: Always confirm before deleting users
5. **Performance**: Click Refresh to ensure you have latest data

## Troubleshooting

### Users Not Loading
- Click "Refresh" button
- Check internet connection
- Verify Firebase is accessible

### Changes Not Saving
- Ensure you click "Save" on edit screen
- Check for error messages in toast notifications
- Verify user account permissions

### Statistics Not Updating
- Click "Refresh" button
- Close and reopen the app
- Check Firebase connection

## Best Practices

1. **Regular Monitoring**: Check analytics weekly
2. **User Verification**: Review new user registrations regularly
3. **Role Management**: Only promote trusted users to admin
4. **Account Maintenance**: Deactivate unused accounts
5. **Security**: Log out after admin sessions
6. **Backups**: Monitor server uptime metrics

## Keyboard Shortcuts

- Use Tab to navigate between fields on edit screen
- Use Enter to save changes on edit screen
- Use Escape to cancel and go back

## Account Management

### Admin Roles
- **Full Access**: Can manage all users, settings, and analytics
- **User Management**: Can edit and delete user accounts
- **Settings Management**: Can modify system settings
- **Analytics View**: Can view all system metrics

### Security Features
- Secure logout functionality
- User action confirmation dialogs
- Real-time data validation
- Audit trails for administrative actions

## Support & Help

For issues or questions:
1. Check the troubleshooting section above
2. Review error messages in toast notifications
3. Contact system administrator
4. Check Firebase console for backend errors

---

**Last Updated**: March 2026
**Version**: 1.0
**Status**: Production Ready

