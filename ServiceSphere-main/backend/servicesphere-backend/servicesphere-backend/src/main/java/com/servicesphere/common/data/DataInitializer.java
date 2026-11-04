package com.servicesphere.common.data;

import com.servicesphere.assets.entity.Asset;
import com.servicesphere.assets.entity.AssetAssignment;
import com.servicesphere.assets.repository.AssetAssignmentRepository;
import com.servicesphere.assets.repository.AssetRepository;
import com.servicesphere.tickets.entity.Ticket;
import com.servicesphere.tickets.repository.TicketRepository;
import com.servicesphere.users.entity.Department;
import com.servicesphere.users.entity.Role;
import com.servicesphere.users.entity.User;
import com.servicesphere.users.repository.DepartmentRepository;
import com.servicesphere.users.repository.RoleRepository;
import com.servicesphere.users.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final AssetRepository assetRepository;
    private final AssetAssignmentRepository assetAssignmentRepository;
    private final TicketRepository ticketRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository,
                           DepartmentRepository departmentRepository,
                           UserRepository userRepository,
                           AssetRepository assetRepository,
                           AssetAssignmentRepository assetAssignmentRepository,
                           TicketRepository ticketRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
        this.assetRepository = assetRepository;
        this.assetAssignmentRepository = assetAssignmentRepository;
        this.ticketRepository = ticketRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (roleRepository.count() > 0) {
            return; // Data already initialized
        }

        // 1. Roles
        Role adminRole = roleRepository.save(new Role("Administrator", "Full system and administrative access"));
        Role assetManagerRole = roleRepository.save(new Role("IT Asset Manager", "Manages IT asset inventory, lifecycles, and assignments"));
        Role agentRole = roleRepository.save(new Role("Support Agent", "Handles support tickets and technical resolution"));
        Role managerRole = roleRepository.save(new Role("Support Lead / Manager", "Supervises ticket queues and SLA performance"));
        Role employeeRole = roleRepository.save(new Role("Employee", "General corporate user / requester"));

        // 2. Departments
        Department itDept = departmentRepository.save(new Department("IT Operations"));
        Department engDept = departmentRepository.save(new Department("Engineering"));
        Department prodDept = departmentRepository.save(new Department("Product & Design"));
        Department finDept = departmentRepository.save(new Department("Finance & HR"));

        // 3. Users (fictional accounts)
        String defaultHashedPwd = passwordEncoder.encode("Password123!");

        User assetManager = userRepository.save(new User(
                "Marcus Vance",
                "marcus.vance@servicesphere.local",
                defaultHashedPwd,
                assetManagerRole,
                itDept
        ));

        User employee1 = userRepository.save(new User(
                "Sarah Chen",
                "sarah.chen@servicesphere.local",
                defaultHashedPwd,
                employeeRole,
                engDept
        ));

        User employee2 = userRepository.save(new User(
                "David Kim",
                "david.kim@servicesphere.local",
                defaultHashedPwd,
                employeeRole,
                prodDept
        ));

        User agent = userRepository.save(new User(
                "Alex Rivera",
                "alex.rivera@servicesphere.local",
                defaultHashedPwd,
                agentRole,
                itDept
        ));

        User lead = userRepository.save(new User(
                "Elena Rostova",
                "elena.rostova@servicesphere.local",
                defaultHashedPwd,
                managerRole,
                itDept
        ));

        // 4. Assets
        LocalDate today = LocalDate.now();

        Asset a1 = assetRepository.save(new Asset(
                "AST-001",
                "Laptop",
                "Apple",
                "MacBook Pro 16\" M3 Max",
                "C02G4581MD6R",
                "In Stock",
                today.minusMonths(6),
                today.plusMonths(30),
                "HQ Storage Room B",
                "Configured with standard developer profile, 36GB RAM, 1TB SSD"
        ));

        Asset a2 = assetRepository.save(new Asset(
                "AST-002",
                "Laptop",
                "Lenovo",
                "ThinkPad X1 Carbon Gen 11",
                "PF49X201",
                "Assigned",
                today.minusYears(1),
                today.plusMonths(12),
                "Floor 3 Desk 304",
                "Standard engineering laptop issue"
        ));

        // Create assignment for a2 to Sarah Chen
        AssetAssignment asgn2 = new AssetAssignment(
                a2,
                employee1,
                LocalDateTime.now().minusMonths(4),
                "Brand new with USB-C 65W charger and privacy screen",
                assetManager
        );
        assetAssignmentRepository.save(asgn2);

        Asset a3 = assetRepository.save(new Asset(
                "AST-003",
                "Monitor",
                "Dell",
                "UltraSharp 32\" 4K U3223QE",
                "CN09187452",
                "In Stock",
                today.minusMonths(3),
                today.plusMonths(33),
                "HQ Inventory Rack 4",
                "IPS Black display with 90W USB-C hub connectivity"
        ));

        Asset a4 = assetRepository.save(new Asset(
                "AST-004",
                "Phone",
                "Apple",
                "iPhone 15 Pro 256GB",
                "DX38947621",
                "In Repair",
                today.minusMonths(14),
                today.minusMonths(2), // Expired warranty
                "Service Center",
                "Battery inspection & charging port diagnostic in progress"
        ));

        Asset a5 = assetRepository.save(new Asset(
                "AST-005",
                "Desktop",
                "Apple",
                "Mac Studio M2 Ultra",
                "MJ92837411",
                "Assigned",
                today.minusMonths(11),
                today.plusDays(18), // Warranty Expiring Soon (< 30 days)
                "Studio Lab 1",
                "Primary video rendering and UI animation workstation"
        ));

        // Create assignment for a5 to David Kim
        AssetAssignment asgn5 = new AssetAssignment(
                a5,
                employee2,
                LocalDateTime.now().minusMonths(6),
                "Workstation deployed in Studio Lab 1 with dual monitors",
                assetManager
        );
        assetAssignmentRepository.save(asgn5);

        Asset a6 = assetRepository.save(new Asset(
                "AST-006",
                "Network Equipment",
                "Cisco",
                "Catalyst 9300 48-Port PoE+",
                "FOC24483921",
                "In Stock",
                today.minusYears(1),
                today.plusYears(2),
                "Server Room Alpha",
                "Core edge switch for Floor 2 expansion"
        ));

        Asset a7 = assetRepository.save(new Asset(
                "AST-007",
                "Peripheral",
                "Logitech",
                "MX Master 3S Wireless Mouse",
                "LZ89230198",
                "Retired",
                today.minusYears(3),
                today.minusYears(1),
                "Disposal Bin",
                "Left switch intermittent bounce after 3 years intensive usage"
        ));

        // Historical assignment for retired asset (showing past assignment was returned)
        AssetAssignment asgn7 = new AssetAssignment(
                a7,
                employee1,
                LocalDateTime.now().minusMonths(18),
                "Issued brand new",
                assetManager
        );
        asgn7.setReturnedAt(LocalDateTime.now().minusMonths(2));
        asgn7.setConditionIn("Left click malfunctioning; marked retired");
        assetAssignmentRepository.save(asgn7);

        Asset a8 = assetRepository.save(new Asset(
                "AST-008",
                "Laptop",
                "Dell",
                "Latitude 7440",
                "8HX93214",
                "In Stock",
                today.minusMonths(2),
                today.plusMonths(34),
                "HQ IT Depot",
                "Ready for new employee onboarding"
        ));

        // 5. Sample Ticket linked to Asset AST-002
        Ticket ticket = new Ticket();
        ticket.setTicketNumber("TICK-1001");
        ticket.setTitle("Lenovo ThinkPad trackpoint drift and thermal throttling");
        ticket.setDescription("Sarah reported frequent cursor drift and fan noise under standard IDE build workload.");
        ticket.setPriority("High");
        ticket.setStatus("Open");
        ticket.setRequester(employee1);
        ticket.setAssignee(agent);
        ticket.setDepartment(engDept);
        ticket.setAsset(a2);
        ticket.setResponseDueAt(LocalDateTime.now().plusHours(2));
        ticket.setResolutionDueAt(LocalDateTime.now().plusDays(1));
        ticketRepository.save(ticket);
    }
}

