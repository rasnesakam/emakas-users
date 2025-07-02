import {
    Sidebar,
    SidebarContent,
    SidebarFooter,
    SidebarGroup, SidebarGroupContent,
    SidebarGroupLabel,
    SidebarHeader, SidebarMenu, SidebarMenuButton, SidebarMenuItem, useSidebar,
} from "@components/shadcn/ui/sidebar.tsx";

import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuTrigger
} from "@components/shadcn/ui/dropdown-menu.tsx";
import {useAuthContext} from "@contexts/AuthContext";
import {
    Braces,
    ChevronUp,
    CircleUserRound,
    House,
    KeyRound,
    LayoutGrid,
    LogOut,
    PanelLeft,
    User2,
    Users
} from "lucide-react";
import {Link} from "react-router";

export function AppSideBar() {
    const { toggleSidebar } = useSidebar()
    const {auth} = useAuthContext();
    return <Sidebar collapsible="icon">
        <SidebarHeader>
            <SidebarMenu>
                <SidebarMenuItem>
                    <SidebarMenuButton>
                        <span className="text-3xl font-bold">emakas-IAM</span>
                    </SidebarMenuButton>
                </SidebarMenuItem>
            </SidebarMenu>
        </SidebarHeader>
        <SidebarContent>
            <SidebarGroup>
                <SidebarGroupLabel></SidebarGroupLabel>
                <SidebarGroupContent>
                    <SidebarMenu>
                        <SidebarMenuItem>
                            <SidebarMenuButton asChild>
                                <Link to={"/page/"}><House /> Home</Link>
                            </SidebarMenuButton>
                        </SidebarMenuItem>
                        <SidebarMenuItem>
                            <SidebarMenuButton asChild>
                                <Link to={"/page/teams"}><Users/> Teams</Link>
                            </SidebarMenuButton>
                        </SidebarMenuItem>
                        <SidebarMenuItem>
                            <SidebarMenuButton asChild>
                                <Link to={"/page/applications"}><LayoutGrid /> Applications</Link>
                            </SidebarMenuButton>
                        </SidebarMenuItem>
                        <SidebarMenuItem>
                            <SidebarMenuButton asChild>
                                <Link to={"/page/permissions"}><KeyRound /> Permissions</Link>
                            </SidebarMenuButton>
                        </SidebarMenuItem>
                        <SidebarMenuItem>
                            <SidebarMenuButton asChild>
                                <Link to={"/page/resources"}><Braces /> Resources</Link>
                            </SidebarMenuButton>
                        </SidebarMenuItem>
                    </SidebarMenu>
                </SidebarGroupContent>
            </SidebarGroup>
        </SidebarContent>
        <SidebarFooter>
            <SidebarMenu>
                <SidebarMenuItem>
                    <SidebarMenuButton onClick={toggleSidebar}>
                        <PanelLeft /> Collapse Sidebar
                    </SidebarMenuButton>
                </SidebarMenuItem>
                <SidebarMenuItem>
                   <DropdownMenu>
                       <DropdownMenuTrigger asChild>
                           <SidebarMenuButton>
                               <User2/> {auth!.username}
                               <ChevronUp className="ml-auto" />
                           </SidebarMenuButton>
                       </DropdownMenuTrigger>
                       <DropdownMenuContent
                           side="top"
                           className="w-[--radix-popper-anchor-width]"
                       >
                           <DropdownMenuItem>
                               <CircleUserRound /> <span>Account</span>
                           </DropdownMenuItem>
                           <DropdownMenuItem>
                               <LogOut /> <span>Sign out</span>
                           </DropdownMenuItem>
                       </DropdownMenuContent>
                   </DropdownMenu>
                </SidebarMenuItem>
            </SidebarMenu>
        </SidebarFooter>
    </Sidebar>
}