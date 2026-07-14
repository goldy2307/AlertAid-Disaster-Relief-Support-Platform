package com.alertaid.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping({"/Alertaid", ""})
public class ViewController {

    @GetMapping("/login")
    public String login() { return "forward:/index.html"; } // React SPA

    // Login and Register are handled by Thymeleaf controller now

    @GetMapping("/campaigns")
    public String campaigns() { return "forward:/campaigns.html"; }

    @GetMapping("/donations")
    public String donations() { return "forward:/donation.html"; }

    @GetMapping("/volunteer/signup")
    public String volunteerSignup() { return "forward:/index.html"; } // React SPA

    @GetMapping("/citizen/dashboard")
    public String citizenDashboard() { return "forward:/citizen_dashboard.html"; }

    @GetMapping("/volunteer/dashboard")
    public String volunteerDashboard() { return "forward:/volunteer_dashboard.html"; }

    @GetMapping("/org/dashboard")
    public String orgDashboard() { return "forward:/organization_dashboard.html"; }

    @GetMapping("/admin/dashboard")
    public String adminDashboard() { return "forward:/admin_dashboard.html"; }

    @GetMapping("/alerts")
    public String alerts() { return "forward:/index.html"; } // React SPA

    @GetMapping("/report")
    public String report() { return "forward:/index.html"; } // React SPA
    @GetMapping("/profile")
    public String profile() { return "forward:/profile.html"; }

    @GetMapping("/forgetpass")
    public String forgetpass() { return "forward:/forgetpass.html"; }

    @GetMapping("/change_password")
    public String changePassword() { return "forward:/change_password.html"; }

    @GetMapping("/helpforpeople")
    public String helpForPeople() { return "forward:/helpforpeople.html"; }

    @GetMapping("/donatemoney")
    public String donateMoney() { return "forward:/index.html"; } // React SPA

    @GetMapping("/contributions")
    public String contributions() { return "forward:/Contribution.html"; }

    // Optional: direct, extensionless aliases matching the underlying filenames
    // e.g. /citizen_dashboard -> citizen_dashboard.html

    @GetMapping("/citizen_dashboard")
    public String citizenDashboardAlias() { return "forward:/citizen_dashboard.html"; }

    // Legacy URLs that included .html in the path – redirect to the clean route
    @GetMapping("/volunteer/citizen_dashboard.html")
    public String legacyVolunteerCitizenDashboard() { return "forward:/citizen_dashboard.html"; }

    @GetMapping("/volunteer_dashboard")
    public String volunteerDashboardAlias() { return "forward:/volunteer_dashboard.html"; }

    @GetMapping("/organization_dashboard")
    public String orgDashboardAlias() { return "forward:/organization_dashboard.html"; }

    @GetMapping("/admin_dashboard")
    public String adminDashboardAlias() { return "forward:/admin_dashboard.html"; }

    @GetMapping("/donation")
    public String donationAlias() { return "forward:/donation.html"; }

    @GetMapping("/volunteer_signup")
    public String volunteerSignupAlias() { return "forward:/index.html"; } // React SPA

    @GetMapping("/admin_reports")
    public String adminReportsAlias() { return "forward:/admin-reports.html"; }

    @GetMapping("/view_donations")
    public String viewDonationsAlias() { return "forward:/view_donations.html"; }

    @GetMapping("/assign_tasks")
    public String assignTasksAlias() { return "forward:/assign_tasks.html"; }

    @GetMapping("/assigned_tasks")
    public String assignedTasksAlias() { return "forward:/assigned_tasks.html"; }

    @GetMapping("/application_org_volunteer")
    public String applicationOrgVolunteerAlias() { return "forward:/application_org_volunteer.html"; }

    @GetMapping("/send_alerts")
    public String sendAlertsAlias() { return "forward:/send_alerts.html"; }

    @GetMapping("/reports")
    public String reportsAlias() { return "forward:/index.html"; } // React SPA

    @GetMapping("/chatbot")
    public String chatbotAlias() { return "forward:/chatbot.html"; }

    @GetMapping("/markavailabilty")
    public String markAvailabilityAlias() { return "forward:/markavailabilty.html"; }
}
