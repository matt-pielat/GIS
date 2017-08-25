using System.Data.Entity;
using GisService.Model;

namespace GisService
{
    public class UserContext : DbContext
    {
        public DbSet<User> Users { get; set; }

        protected override void OnModelCreating(DbModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);
            modelBuilder.Entity<LocationDateDb>()
                        .HasRequired<User>(u => u.User)
                        .WithMany(s => s.Locations)
                        .HasForeignKey(s => s.UserId);
        }
    }
}